package com.kevinhuang.simplerpc.server.registry;

import com.kevinhuang.simplerpc.common.ServiceInterfaceInfo;

import java.util.HashMap;
import java.util.Map;

public class DefaultServiceRegistry implements ServiceRegistry {

    private final Map<String, ServiceInterfaceInfo> localMap = new HashMap<>();

    @Override
    public void register(ServiceInterfaceInfo serviceInterfaceInfo) throws Exception {
        if (serviceInterfaceInfo == null) {
            throw new IllegalArgumentException("param.invalid");
        }
        String serverName = serviceInterfaceInfo.getServiceName();
        localMap.put(serverName, serviceInterfaceInfo);
    }

    @Override
    public ServiceInterfaceInfo getRegisteredObj(String serviceName) {
        return localMap.get(serviceName);
    }
}
