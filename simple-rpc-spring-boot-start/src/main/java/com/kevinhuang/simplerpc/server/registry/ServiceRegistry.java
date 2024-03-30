package com.kevinhuang.simplerpc.server.registry;

import com.kevinhuang.simplerpc.common.ServiceInterfaceInfo;

/**
 * The interface Service registry.
 */
public interface ServiceRegistry {


    /**
     * Register.
     *
     * @param serviceInterfaceInfo the service info
     * @throws Exception the exception
     */
    void register(ServiceInterfaceInfo serviceInterfaceInfo) throws Exception;

    ServiceInterfaceInfo getRegisteredObj(String serviceName);

}
