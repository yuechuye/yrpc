package com.yy.exception;

/**
 * @author yuechu
 */
public class NetException extends RuntimeException{
    public NetException() {
    }

    public NetException(String message) {
        super(message);
    }
}
