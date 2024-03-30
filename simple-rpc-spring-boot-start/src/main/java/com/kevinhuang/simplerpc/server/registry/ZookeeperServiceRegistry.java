package com.kevinhuang.simplerpc.server.registry;

import com.alibaba.fastjson.JSON;
import com.kevinhuang.simplerpc.common.Cons;
import com.kevinhuang.simplerpc.common.ServiceInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ZookeeperServiceRegistry extends DefaultServiceRegistry {

    private ZkClient zkClient;


    public ZookeeperServiceRegistry(String zkAddress) {
       init(zkAddress);
    }

    private void init(String zkAddress) {
        zkClient = new ZkClient(zkAddress);
        // 设置序列化反序列化器
        zkClient.setZkSerializer(new ZkSerializer() {
            @Override
            public byte[] serialize(Object o) throws ZkMarshallingError {
                return String.valueOf(o).getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes, StandardCharsets.UTF_8);
            }
        });
    }
    /**
     * Register.
     *
     * @param serviceInterfaceInfo the service info
     * @throws Exception the exception
     */
    @Override
    public void register(ServiceInterfaceInfo serviceInterfaceInfo) throws Exception {
        log.info("Registering service: {}", serviceInterfaceInfo);

        super.register(serviceInterfaceInfo);
        // 创建ZK永久节点（服务节点）
        String servicePath = Cons.ZOO_KEEPER_PREFIX + serviceInterfaceInfo.getServiceName();
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath, true);
            log.info("Create node:{}", servicePath);
        }

        // 创建临时节点
        String uri = JSON.toJSONString(serviceInterfaceInfo);
        uri = URLEncoder.encode(uri, "UTF-8");
        String uriPath = servicePath + "/" + uri;
        if (zkClient.exists(uriPath)) {
            zkClient.delete(uriPath);
        }
        zkClient.createEphemeral(uriPath);
        log.info("Create ephemeral node : {}", uriPath);

    }


    @Override
    public ServiceInterfaceInfo getRegisteredObj(String serviceName) {
//        zkClient.getChildren();
        return super.getRegisteredObj(serviceName);
    }
}
