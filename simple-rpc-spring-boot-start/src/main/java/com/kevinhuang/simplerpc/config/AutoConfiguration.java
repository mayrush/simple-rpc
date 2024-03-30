package com.kevinhuang.simplerpc.config;

import com.kevinhuang.simplerpc.client.ClientProxyFactory;
import com.kevinhuang.simplerpc.client.ServiceDiscovery;
import com.kevinhuang.simplerpc.client.ZookeeperServiceDiscovery;
import com.kevinhuang.simplerpc.client.network.NettyRpcClient;
import com.kevinhuang.simplerpc.listener.DefaultRpcListener;
import com.kevinhuang.simplerpc.property.RpcProperties;
import com.kevinhuang.simplerpc.serialization.DefaultMessageProtocol;
import com.kevinhuang.simplerpc.serialization.MessageProtocol;
import com.kevinhuang.simplerpc.server.network.NettyRpcServer;
import com.kevinhuang.simplerpc.server.network.RequestHandler;
import com.kevinhuang.simplerpc.server.network.RpcServer;
import com.kevinhuang.simplerpc.server.registry.DefaultServiceRegistry;
import com.kevinhuang.simplerpc.server.registry.ServiceRegistry;
import com.kevinhuang.simplerpc.server.registry.ZookeeperServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AutoConfiguration {


    @Bean
    public DefaultRpcListener defaultRpcListener(@Autowired ServiceRegistry serviceRegistry,
            @Autowired RpcServer rpcServer,
            @Autowired ClientProxyFactory clientProxyFactory,
            @Autowired RpcProperties properties) {
        return new DefaultRpcListener(serviceRegistry, rpcServer, clientProxyFactory, properties);

    }

    @Bean
    public RpcProperties rpcProperties() {
        return new RpcProperties();
    }


    @Bean
    public ServiceDiscovery serviceDiscovery(@Autowired RpcProperties rpcProperties) {
        final String register = rpcProperties.getRegister();
        if ("zookeeper".equalsIgnoreCase(register)) {
            return new ZookeeperServiceDiscovery(rpcProperties.getRegisterAddress());
        }
        //  else if("nacos".equalsIgnoreCase(register)){
        //
        // }
        return null;
    }


    @Bean
    public ServiceRegistry serviceRegistry(@Autowired RpcProperties rpcProperties) {
        final String register = rpcProperties.getRegister();
        if ("zookeeper".equalsIgnoreCase(register)) {
            return new ZookeeperServiceRegistry(rpcProperties.getRegisterAddress());
        }
        //  else if("nacos".equalsIgnoreCase(register)){
        //
        // }
        else {
            log.info("Default register active.");
            return new DefaultServiceRegistry();
        }

    }


    @Bean
    public ClientProxyFactory clientProxyFactory(@Autowired ServiceDiscovery serviceDiscovery) {
        return new ClientProxyFactory(serviceDiscovery, new DefaultMessageProtocol(), new NettyRpcClient());
    }


    @Bean
    public RequestHandler requestHandler(@Autowired RpcProperties rpcProperties,
            @Autowired ServiceRegistry serviceRegistry) {
        final String protocol = rpcProperties.getProtocol();
        MessageProtocol messageProtocol = new DefaultMessageProtocol();
        // 暂时只支持JAVA 自带序列化方式
        if ("java".equalsIgnoreCase(protocol)) {
            messageProtocol = new DefaultMessageProtocol();
        }
        return new RequestHandler(messageProtocol, serviceRegistry);
    }


    @Bean
    public RpcServer rpcServer(@Autowired RpcProperties rpcProperties,
            @Autowired RequestHandler requestHandler){
        return  new NettyRpcServer(rpcProperties.getExposePort(),requestHandler);
    }


}
