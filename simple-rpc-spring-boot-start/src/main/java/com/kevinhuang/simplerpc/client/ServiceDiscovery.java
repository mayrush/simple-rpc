package com.kevinhuang.simplerpc.client;


import com.kevinhuang.simplerpc.common.ServiceInterfaceInfo;

/**
 * The interface Service discovery.
 */
public interface ServiceDiscovery {


    /**
     * Select one instance instance info.
     *
     * @param serviceName the service name
     * @return the instance info
     */
    ServiceInterfaceInfo selectOneInstance(String serviceName);


}
