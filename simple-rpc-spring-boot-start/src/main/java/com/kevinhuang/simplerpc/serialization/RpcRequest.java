package com.kevinhuang.simplerpc.serialization;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class RpcRequest implements Serializable {

    private Class<?>[] parameterTypes;

    private String serviceName;


    private String methodName;

    private Object parameters;

}
