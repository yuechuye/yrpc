package com.yy.common.exceptions;

public class NetException extends RuntimeException{
    public NetException() {
    }

    public NetException(String message) {
        super(message);
    }
}
