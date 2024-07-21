//import com.yy.Configuration;
//import com.yy.ServiceConfig;
//import com.yy.discover.RegistryConfig;
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import lombok.extern.slf4j.Slf4j;
//
//import java.lang.reflect.InvocationTargetException;
//import java.net.InetSocketAddress;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.stream.Collectors;
//
///**
// * MrpcBootStrap是MRPC框架的启动类，负责配置和启动MRPC服务。
// * 它使用Netty作为底层通信框架，并支持服务的发布和引用。
// */
//@Slf4j
//public class MrpcBootStrap {
//
//    // 单例模式，确保MrpcBootStrap的唯一性
//    private static final MrpcBootStrap mrpcBootStrap = new MrpcBootStrap();
//    // 配置对象，存储框架的配置信息
//    private Configuration configuration;
//
//
//    // 维护已发布服务的列表，使用全类名作为键
//    //维护已经发布并且暴露的服务列表 key：interface的全限定名 value：定义好的ServiceConfig
//    public static final Map<String, ServiceConfig<?>> SERVICE_LIST = new ConcurrentHashMap<>(16);
//
//
//    // 网络通道的缓存，用于快速查找通道
//    //netty缓存
//    public static final Map<InetSocketAddress, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>(16);
//    // 心跳响应时间缓存，用于管理通道的心跳
//    //心跳检测响应时间缓存
//    public static final TreeMap<Long, Channel> RES_TIME_CHANNEL_CACHE = new TreeMap<>();
//
//    // 全局的CompletableFuture缓存，用于存储异步调用的结果
//    //全局的CompletableFuture存储
//    public static final Map<Long, CompletableFuture<Object>> PENDING_FUTURE = new ConcurrentHashMap<>(64);
//
//    // 全局的请求缓存，使用ThreadLocal存储当前线程的请求
//    //全局的请求缓存
////    public static ThreadLocal<MrpcRequest> THREAD_LOCAL_CACHE = new ThreadLocal<>();
//
//    // 私有构造方法，确保只能通过getInstance方法获取实例
//    private MrpcBootStrap() {
//        configuration = new Configuration();
//    }
//
//    // 获取配置对象
//    public Configuration getConfiguration() {
//        return configuration;
//    }
//
//    // 获取MrpcBootStrap的单例实例
//    public static MrpcBootStrap getInstance() {
//        return mrpcBootStrap;
//    }
//
//    // 配置应用名称
//    /**
//     * 定义当前应用名字
//     *
//     * @param appName 应用名称
//     * @return 当前MrpcBootStrap实例
//     */
//    public MrpcBootStrap application(String appName) {
//        configuration.setAppName(appName);
//        return this;
//    }
//
//    // 配置注册中心
//    /**
//     * 用来配置注册中心
//     *
//     * @param registryConfig 注册中心配置
//     * @return 当前MrpcBootStrap实例
//     */
//    public MrpcBootStrap registry(RegistryConfig registryConfig) {
//        configuration.setRegistryConfig(registryConfig);
//        return this;
//    }
//
//    // 配置序列化协议
//    /**
//     * 配置序列化协议
//     *
//     * @param protocolConfig 协议配置
//     * @return 当前MrpcBootStrap实例
//     */
//    public MrpcBootStrap protocol(ProtocolConfig protocolConfig) {
//        log.debug("使用了{}进行序列化", protocolConfig.toString());
//        configuration.setSerializeType(protocolConfig.getProtocol());
//        return this;
//    }
//
//    // 发布单个服务
//    /**
//     * 发布服务
//     *
//     * @param service 需要发布的服务配置
//     * @return 当前MrpcBootStrap实例
//     */
//    public MrpcBootStrap publish(ServiceConfig<?> service) {
//        configuration.getRegistryConfig().getRegistry().registry(service);
//        SERVICE_LIST.put(service.getInterfaceProvider().getName(), service);
//        return this;
//    }
//
//    // 批量发布服务
//    /**
//     * 批量发布
//     *
//     * @param services 需要发布的服务的集合
//     * @return 当前MrpcBootStrap实例
//     */
//    public MrpcBootStrap publish(List<ServiceConfig<?>> services) {
//        for (ServiceConfig<?> service : services) {
//            this.publish(service);
//        }
//        return this;
//    }
//
//    // 启动Netty服务端
//    /**
//     * 启动netty服务
//     */
//    public void start() {
//        EventLoopGroup boss = new NioEventLoopGroup(2);
//        EventLoopGroup worker = new NioEventLoopGroup(10);
//        ServerBootstrap serverBootstrap = new ServerBootstrap();
//        try {
//            serverBootstrap.group(boss, worker)
//                    .channel(NioServerSocketChannel.class)
//                    .childHandler(new ProviderChannelInitializer());
//
//            ChannelFuture future = serverBootstrap.bind(configuration.getPort()).sync();
//            future.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                boss.shutdownGracefully().sync();
//                worker.shutdownGracefully().sync();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    // 配置服务引用
//    /**
//     * 服务调用方
//     */
//    public MrpcBootStrap reference(ReferenceConfig<?> reference) {
//        log.debug("开始心跳检测");
//        HeartBeatDetector.detectHeartbeat(reference.getInterfaceRef().getName());
//        reference.setRegistry(configuration.getRegistryConfig().getRegistry());
//        return this;
//    }
//
//    // 配置序列化类型
//    public MrpcBootStrap serialize(String serializeType) {
//        configuration.setSerializeType(serializeType);
//        return this;
//    }
//
//    // 配置压缩类型
//    public MrpcBootStrap compress(String compressType) {
//        configuration.setCompressType(compressType);
//        return this;
//    }
//
//    // 扫描指定包下所有类并发布为服务
//    /**
//     * 扫描指定包名下的类，并将标注了MrpcApi注解的类发布为服务
//     *
//     * @param packageName 待扫描的包名
//     * @return 当前MrpcBootStrap实例
//     */
//    public MrpcBootStrap scan(String packageName) {
//        List<String> classNames = getAllClassNames(packageName);
//        List<? extends Class<?>> classes = classNames.stream().map(className -> {
//                    try {
//                        return Class.forName(className);
//                    } catch (ClassNotFoundException e) {
//                        throw new RuntimeException(e);
//                    }
//                }).filter(clazz -> clazz.getAnnotation(MrpcApi.class) != null)
//                .collect(Collectors.toList());
//
//        for (Class<?> clazz : classes) {
//            Class<?>[] interfaces = clazz.getInterfaces();
//            Object instance = null;
//            try {
//                instance = clazz.getConstructor().newInstance();
//            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//                     NoSuchMethodException e) {
//                throw new RuntimeException(e);
//            }
//
//            MrpcApi mrpcApi = clazz.getAnnotation(MrpcApi.class);
//            String group = mrpcApi.group();
//
//            for (Class<?> anInterface : interfaces) {
//                ServiceConfig<?> serviceConfig = new ServiceConfig<>();
//                serviceConfig.setInterface(anInterface);
//                serviceConfig.setRef(instance);
//                serviceConfig.setGroup(group);
//                log.debug("已经通过包扫描将服务【{}】发布",anInterface);
//                publish(serviceConfig);
//            }
//
//        }
//
//        return this;
//    }
//
//    // 获取指定包下所有类的名称
//    private List<String> getAllClassNames(String packageName) {
//        String basePath = packageName.replaceAll("\\.", "/");
//        URL url = ClassLoader.getSystemClassLoader().getResource(basePath);
//        if (url == null) {
//            throw new RuntimeException("寻找包路径时出现异常");
//        }
//        String absolutionPath = url.getFile();
//        List<String> classNames = new ArrayList<>();
//        classNames = recursionFile(absolutionPath, classNames, basePath);
//        System.out.println(absolutionPath);
//        return classNames;
//    }
//
//    // 递归扫描文件夹下的类
//    private List<String> recursionFile(String absolutionPath, List<String> classNames, String basePath) {
//        //获取文件
//        File file = new File(absolutionPath);
//        //判断文件是否是文件夹
//        if (file.isDirectory()) {
//            //找到文件夹的所有文件
//            File[] children = file.listFiles(pathname ->
//                    pathname.isDirectory() || pathname.getName().contains(".class"));
//            if (children == null){
//                return classNames;
//            }
//            for (File child : children) {
//                if (child.isDirectory()) {
//                    recursionFile(child.getAbsolutePath(), classNames, basePath);
//                } else {
//                    String className = getClassNamesByAbsolutePath(child.getAbsolutePath(),basePath);
//                    classNames.add(className);
//                }
//            }
//        } else {
//            //转换成类的全限定名
//            String className = getClassNamesByAbsolutePath(absolutionPath,basePath);
//            classNames.add(className);
//        }
//        return classNames;
//    }
//
//    // 根据文件路径获取类名
//    private String getClassNamesByAbsolutePath(String absolutePath, String basePath) {
//        String fileName = absolutePath.substring(absolutePath.indexOf(basePath.replaceAll("/", "\\\\")))
//                .replaceAll("\\\\", ".");
//        fileName = fileName.substring(0, fileName.lastIndexOf(".class"));
//        return fileName;
//    }
//
//    // 配置服务组别
//    public MrpcBootStrap group(String group) {
//        this.getConfiguration().setGroup(group);
//        return this;
//    }
//}
