package com.yy.exception;

/**
 * @author yuechu
 */
public class ZookeeperException extends RuntimeException{
    public ZookeeperException() {
        super();
    }

    public ZookeeperException(String message) {
        super(message);
    }
}
