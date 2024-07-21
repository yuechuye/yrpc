package com.yy;


import com.yy.annotation.MrpcApi;

@MrpcApi(group = "importance")
public class HelloMrpcServiceImpl implements HelloMrpcService{

    @Override
    public String Hello(String msg) {
        return "hello consumer" + msg;
    }
}
