package com.yy;

import com.yy.discovery.RegistryConfig;
import com.yy.service.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * Hello world!
 *
 */
@Slf4j
public class App 
{
    public static void main( String[] args )
    {
        ReferenceConfig<HelloService> reference = new ReferenceConfig();
        reference.setInterface(HelloService.class);

        RpcBootStrap.getInstance()
                .application("first-consumer-1")
                .registry(new RegistryConfig("zookeeper://43.139.111.22:2181"))
                .serialize("hessian")
                .compress("gzip")
                .group("importance")
                .reference(reference);

        for (int i = 0; i < 10; i++) {

            long l = System.currentTimeMillis();
            HelloService helloService = reference.get();
            String sayHi = helloService.say("yy");

            log.info("最终结果：{}", sayHi);
            log.info("耗时：{}", System.currentTimeMillis() - l);

        }





    }
}
