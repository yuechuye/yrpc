package com.yy.service.impl;

import com.yy.common.annotation.MrpcApi;
import com.yy.service.HelloService;

/**
 * @author yuechu
 */
@MrpcApi(group = "importance")
public class HelloServiceImpl implements HelloService {
    @Override
    public String say(String name) {
        return "你好："+name;
    }
}
