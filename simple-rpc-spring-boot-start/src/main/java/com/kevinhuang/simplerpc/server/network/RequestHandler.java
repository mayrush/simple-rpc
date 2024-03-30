package com.kevinhuang.simplerpc.server.network;

import com.kevinhuang.simplerpc.serialization.MessageProtocol;
import com.kevinhuang.simplerpc.serialization.RpcRequest;
import com.kevinhuang.simplerpc.serialization.RpcResponse;
import com.kevinhuang.simplerpc.common.ServiceInterfaceInfo;
import com.kevinhuang.simplerpc.server.registry.ServiceRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    private final MessageProtocol protocol;

    private final ServiceRegistry serviceRegistry;

    public RequestHandler(MessageProtocol protocol,
            ServiceRegistry serviceRegistry) {
        this.protocol = protocol;
        this.serviceRegistry = serviceRegistry;
    }



    public byte[] handleRequest(byte[] reqBytes) throws Exception {
        // 请求消息解码
        RpcRequest request = protocol.unmarshallingReqMessage(reqBytes);
        String serviceName = request.getServiceName();
        ServiceInterfaceInfo serviceInterfaceInfo = serviceRegistry.getRegisteredObj(serviceName);
        RpcResponse response = new RpcResponse();
        if (serviceInterfaceInfo == null) {
            response.setStatus("Not Found");
            return protocol.marshallingRespMessage(response);
        }

        // 通过反射调用目标方法
        try {
            final Method method =
                    serviceInterfaceInfo.getClazz().getMethod(request.getMethodName(), request.getParameterTypes());
            final Object retValue = method.invoke(serviceInterfaceInfo.getObj(), request.getParameters());

            response.setStatus("Success");
            response.setRetValue(retValue);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            response.setStatus("Fail");
            response.setException(e);
        }
        return protocol.marshallingRespMessage(response);
    }
}
