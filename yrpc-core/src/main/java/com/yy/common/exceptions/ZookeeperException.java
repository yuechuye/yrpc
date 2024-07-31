package com.yy.common.exceptions;

public class ZookeeperException extends RuntimeException{
    public ZookeeperException() {
        super();
    }

    public ZookeeperException(String message) {
        super(message);
    }
}
