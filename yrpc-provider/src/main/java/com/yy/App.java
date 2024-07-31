package com.yy;

import com.yy.discovery.RegistryConfig;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
//        ServiceConfig<HelloService> service = new ServiceConfig();
//        service.setInterface(HelloService.class);
//        service.setRef(new HelloServiceImpl());

        RpcBootStrap.getInstance()
                .application("first-provider-1")
                .registry(new RegistryConfig("zookeeper://43.139.111.22:2181"))
                .protocol(new ProtocolConfig("jdk"))
                .scan("com.yy")
                .start();
    }
}
