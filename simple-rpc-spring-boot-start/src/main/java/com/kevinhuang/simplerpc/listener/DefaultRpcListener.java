package com.kevinhuang.simplerpc.listener;

import com.kevinhuang.simplerpc.annotation.ServiceExpose;
import com.kevinhuang.simplerpc.annotation.ServiceReference;
import com.kevinhuang.simplerpc.client.ClientProxyFactory;
import com.kevinhuang.simplerpc.property.RpcProperties;
import com.kevinhuang.simplerpc.server.network.RpcServer;
import com.kevinhuang.simplerpc.common.ServiceInterfaceInfo;
import com.kevinhuang.simplerpc.server.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@Slf4j
public class DefaultRpcListener implements ApplicationListener<ContextRefreshedEvent> {

    private final ServiceRegistry serviceRegistry;

    private final RpcServer rpcServer;

    private final ClientProxyFactory clientProxyFactory;

    private RpcProperties rpcProperties;

    public DefaultRpcListener(ServiceRegistry serviceRegistry,
            RpcServer rpcServer,
            ClientProxyFactory clientProxyFactory,
            RpcProperties rpcProperties) {
        this.serviceRegistry = serviceRegistry;
        this.rpcServer = rpcServer;
        this.clientProxyFactory = clientProxyFactory;
        this.rpcProperties = rpcProperties;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        final ApplicationContext applicationContext = event.getApplicationContext();

        // 如果是root application context 就开始执行
        if (applicationContext.getParent() == null) {
            //初始化rpc服务
            initRpcServer(applicationContext);
            //初始化rpc客户端
            initRpcClient(applicationContext);
        }
    }


    private void initRpcClient(ApplicationContext applicationContext) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            Class<?> clazz = applicationContext.getType(beanName);
            if (clazz == null) {
                continue;
            }

            // 遍历每个bean的成员属性，如果成员属性被@ServiceReference 注解标记，说明依赖rpc远端接口
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                final ServiceReference annotation = field.getAnnotation(ServiceReference.class);
                if (annotation == null) {
                    // 没有则跳过
                    continue;
                }
                // 找到依赖
                Object beanObj = applicationContext.getBean(beanName);
                Class<?> fieldClazz = field.getType();

                try {
                    field.setAccessible(true);
                    field.set(beanObj, clientProxyFactory.getProxyInstance(fieldClazz));
                } catch (Exception e) {
                    log.error("init rpc client fail: {}", e);
                    e.printStackTrace();
                }
            }
        }
    }

    private void initRpcServer(ApplicationContext applicationContext) {
        // 1.1 扫描@ServiceExpose注解,并将服务接口信息注册到注册中心
        final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ServiceExpose.class);
        if (beans.size() == 0) {
            // 没有发现
            return;
        }

        for (Object beanObj : beans.values()) {
            // 注册服务实例接口信息
            registerInstanceInterfaceInfo(beanObj);
        }
        // 1.2 启动网络通信服务器，开始监听指定端口
        rpcServer.start();

    }


    private void registerInstanceInterfaceInfo(Object beanObj) {
        final Class<?>[] interfaces = beanObj.getClass().getInterfaces();
        if (interfaces.length <= 0) {
            // bean 未实现接口
            return;
        }

        // 暂时只考虑实现一个接口的场景
        Class<?> interfaceClazz = interfaces[0];
        String serviceName = interfaceClazz.getName();
        String ip = getLocalAddress();
        Integer port = rpcProperties.getExposePort();
        try {
            serviceRegistry.register(new ServiceInterfaceInfo(serviceName, ip, port, interfaceClazz, beanObj));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getLocalAddress() {
        String ip = "127.0.0.1";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("Get Host Address Fail: {}", e);
        }
        return ip;
    }
}
