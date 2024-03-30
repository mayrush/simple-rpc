package com.kevinhuang.simplerpc.exception;

public class RpcException extends RuntimeException {
    public RpcException(String message) {
        super(message);
    }
}
