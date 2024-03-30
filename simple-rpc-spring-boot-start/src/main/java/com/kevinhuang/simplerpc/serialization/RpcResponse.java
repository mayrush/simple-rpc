package com.kevinhuang.simplerpc.serialization;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class RpcResponse implements Serializable {

    private String status;


    private Object retValue;

    private Exception exception;

    private Map<String, String> headers = new HashMap<>();

}
