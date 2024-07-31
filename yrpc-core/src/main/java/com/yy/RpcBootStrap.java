package com.yy;


import com.yy.channelHandler.ProviderChannelInitializer;
import com.yy.common.annotation.MrpcApi;
import com.yy.discovery.RegistryConfig;
import com.yy.heartbeat.HeartBeatDetector;
import com.yy.transport.RpcRequest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class RpcBootStrap {

    private static final RpcBootStrap RpcBootStrap = new RpcBootStrap();
    private Configuration configuration;


    //维护已经发布并且暴露的服务列表 key：interface的全限定名 value：定义好的ServiceConfig
    public static final Map<String, ServiceConfig<?>> SERVICE_LIST = new ConcurrentHashMap<>(16);


    //netty缓存
    public static final Map<InetSocketAddress, Channel> CHANNEL_CACHE = new ConcurrentHashMap<>(16);
    //心跳检测响应时间缓存
    public static final TreeMap<Long, Channel> RES_TIME_CHANNEL_CACHE = new TreeMap<>();

    //全局的CompletableFuture存储
    public static final Map<Long, CompletableFuture<Object>> PENDING_FUTURE = new ConcurrentHashMap<>(64);

    //全局的请求缓存
    public static ThreadLocal<RpcRequest> THREAD_LOCAL_CACHE = new ThreadLocal<>();

    private RpcBootStrap() {
        configuration = new Configuration();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public static RpcBootStrap getInstance() {
        return RpcBootStrap;
    }

    /**
     * 定义当前应用名字
     *
     * @param appName 名字
     * @return this
     */
    public RpcBootStrap application(String appName) {
        configuration.setAppName(appName);
        return this;
    }

    /**
     * 用来配置注册中心
     *
     * @return this
     */
    public RpcBootStrap registry(RegistryConfig registryConfig) {
        configuration.setRegistryConfig(registryConfig);
        return this;
    }

    /**
     * 配置序列化协议
     *
     * @param protocolConfig 协议
     * @return this
     */
    public RpcBootStrap protocol(ProtocolConfig protocolConfig) {
        log.debug("使用了{}进行序列化", protocolConfig.toString());
        configuration.setSerializeType(protocolConfig.getProtocol());
        return this;
    }


    /**
     * 发布服务
     *
     * @param service 需要发布的服务
     * @return this
     */
    public RpcBootStrap publish(ServiceConfig<?> service) {
        configuration.getRegistryConfig().getRegistry().registry(service);
        SERVICE_LIST.put(service.getInterfaceProvider().getName(), service);
        return this;
    }

    /**
     * 批量发布
     *
     * @param services 需要发布的服务的集合
     * @return this
     */
    public RpcBootStrap publish(List<ServiceConfig<?>> services) {
        for (ServiceConfig<?> service : services) {
            this.publish(service);
        }
        return this;
    }

    /**
     * 启动netty服务
     */
    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup worker = new NioEventLoopGroup(10);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ProviderChannelInitializer());

            ChannelFuture future = serverBootstrap.bind(configuration.getPort()).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                boss.shutdownGracefully().sync();
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 服务调用方
     */


    /**
     * 引用服务配置。
     *
     * 该方法用于配置服务引用的相关设置，特别是心跳检测和注册中心的设置。
     * 它通过引用配置对象对服务进行初始化和配置，为后续的服务调用做好准备。
     *
     * @param reference 服务引用配置对象，包含了服务的接口信息、注册中心配置等。
     * @return 返回当前的RpcBootStrap实例，支持方法链式调用。
     */
    public RpcBootStrap reference(ReferenceConfig<?> reference) {
        // 开始心跳检测，确保服务的可用性和连接的健康性
        log.debug("开始心跳检测");
        String name = reference.getInterfaceRef().getName();
        HeartBeatDetector.detectHeartbeat(name);

        // 设置注册中心，使服务能够正确地注册到指定的注册中心
        reference.setRegistry(configuration.getRegistryConfig().getRegistry());

        return this;
    }

    public RpcBootStrap serialize(String serializeType) {
        configuration.setSerializeType(serializeType);
        return this;
    }

    public RpcBootStrap compress(String compressType) {
        configuration.setCompressType(compressType);
        return this;
    }


    public RpcBootStrap scan(String packageName) {
        List<String> classNames = getAllClassNames(packageName);
        List<? extends Class<?>> classes = classNames.stream().map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(clazz -> clazz.getAnnotation(MrpcApi.class) != null)
                .collect(Collectors.toList());

        for (Class<?> clazz : classes) {
            Class<?>[] interfaces = clazz.getInterfaces();
            Object instance = null;
            try {
                instance = clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            MrpcApi mrpcApi = clazz.getAnnotation(MrpcApi.class);
            String group = mrpcApi.group();

            for (Class<?> anInterface : interfaces) {
                ServiceConfig<?> serviceConfig = new ServiceConfig<>();
                serviceConfig.setInterface(anInterface);
                serviceConfig.setRef(instance);
                serviceConfig.setGroup(group);
                log.debug("已经通过包扫描将服务【{}】发布",anInterface);
                publish(serviceConfig);
            }

        }

        return this;
    }

    private List<String> getAllClassNames(String packageName) {
        String basePath = packageName.replaceAll("\\.", "/");
        URL url = ClassLoader.getSystemClassLoader().getResource(basePath);
        if (url == null) {
            throw new RuntimeException("寻找包路径时出现异常");
        }
        String absolutionPath = url.getFile();
        List<String> classNames = new ArrayList<>();
        classNames = recursionFile(absolutionPath, classNames, basePath);
        System.out.println(absolutionPath);
        return classNames;
    }

    private List<String> recursionFile(String absolutionPath, List<String> classNames, String basePath) {
        //获取文件
        File file = new File(absolutionPath);
        //判断文件是否是文件夹
        if (file.isDirectory()) {
            //找到文件夹的所有文件
            File[] children = file.listFiles(pathname ->
                    pathname.isDirectory() || pathname.getName().contains(".class"));
            if (children == null){
                return classNames;
            }
            for (File child : children) {
                if (child.isDirectory()) {
                    recursionFile(child.getAbsolutePath(), classNames, basePath);
                } else {
                    String className = getClassNamesByAbsolutePath(child.getAbsolutePath(),basePath);
                    classNames.add(className);
                }
            }
        } else {
            //转换成类的全限定名
            String className = getClassNamesByAbsolutePath(absolutionPath,basePath);
            classNames.add(className);
        }
        return classNames;
    }

    private String getClassNamesByAbsolutePath(String absolutePath, String basePath) {
        String fileName = absolutePath.substring(absolutePath.indexOf(basePath.replaceAll("/", "\\\\")))
                .replaceAll("\\\\", ".");
        fileName = fileName.substring(0, fileName.lastIndexOf(".class"));
        return fileName;
    }

    public RpcBootStrap group(String group) {
        this.getConfiguration().setGroup(group);
        return this;
    }
}
