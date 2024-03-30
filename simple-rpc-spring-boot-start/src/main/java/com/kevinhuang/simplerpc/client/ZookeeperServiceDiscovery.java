package com.kevinhuang.simplerpc.client;

import com.alibaba.fastjson.JSON;
import com.kevinhuang.simplerpc.common.Cons;
import com.kevinhuang.simplerpc.common.ServiceInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery {


    private ZkClient zkClient;

    public ZookeeperServiceDiscovery(String zkAddress) {
        zkClient = new ZkClient(zkAddress);
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
     * Select one instance instance info.
     *
     * @param serviceName the service name
     * @return the instance info
     */
    @Override
    public ServiceInterfaceInfo selectOneInstance(String serviceName) {
        String servicePath = Cons.ZOO_KEEPER_PREFIX + serviceName;
        log.info("find zkClient instance by path: {}", servicePath);
        final List<String> childNodes = zkClient.getChildren(servicePath);
        if(childNodes.size() ==0){
            log.error("not fund any instance.");
        }
        return Optional.ofNullable(childNodes)
                .orElse(new ArrayList<>())
                .stream()
                .map(node -> {
                    try {
                        // URL 解码服务信息
                        String serviceInstanceJson = URLDecoder.decode(node, "UTF-8");
                        return JSON.parseObject(serviceInstanceJson, ServiceInterfaceInfo.class);
                    } catch (UnsupportedEncodingException ex) {
                        log.error("Fail to decode", ex);
                    }
                    return null;
                }).filter(Objects::nonNull).findAny().get();
    }
}
