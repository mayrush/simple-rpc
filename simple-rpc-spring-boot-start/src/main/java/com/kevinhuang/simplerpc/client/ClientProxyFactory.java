package com.kevinhuang.simplerpc.client;

import com.kevinhuang.simplerpc.exception.RpcException;
import com.kevinhuang.simplerpc.serialization.MessageProtocol;
import com.kevinhuang.simplerpc.client.network.RpcClient;
import com.kevinhuang.simplerpc.common.ServiceInterfaceInfo;
import com.kevinhuang.simplerpc.serialization.RpcResponse;
import com.kevinhuang.simplerpc.serialization.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class ClientProxyFactory {

    private ServiceDiscovery serviceDiscovery;

    private MessageProtocol messageProtocol;

    private RpcClient rpcClient;


    public ClientProxyFactory(ServiceDiscovery serviceDiscovery,
            MessageProtocol messageProtocol, RpcClient rpcClient) {
        this.serviceDiscovery = serviceDiscovery;
        this.messageProtocol = messageProtocol;
        this.rpcClient = rpcClient;
    }


    @SuppressWarnings("unchecked")
    public <T> T getProxyInstance(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 第一步：通过服务发现机制选择一个服务提供者暴露的服务
                String serviceName = clazz.getName();
                ServiceInterfaceInfo serviceInterfaceInfo = serviceDiscovery.selectOneInstance(serviceName);
                log.info("Rpc server instance list :{}", serviceInterfaceInfo);
                if (serviceInterfaceInfo == null) {
                    throw new RpcException("No rpc servers found.");
                }

                // 第二步：构造rpc 请求对象
                final RpcRequest request = new RpcRequest();
                request.setServiceName(serviceName);
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);

                // 第三步：编码请求消息
                byte[] data = messageProtocol.marshallingReqMessage(request);

                // 第四步：调用 rpc client 开始发送消息
                byte[] byteResponse = rpcClient.sendMessage(data, serviceInterfaceInfo);

                // 第五步：解码响应消息
                final RpcResponse response = messageProtocol.unmarshallingRespMessage(byteResponse);

                // 第六步：解析返回结果进行处理
                if (response.getException() != null) {
                    throw response.getException();
                }
                return response.getRetValue();
            }
        });



    }
}
