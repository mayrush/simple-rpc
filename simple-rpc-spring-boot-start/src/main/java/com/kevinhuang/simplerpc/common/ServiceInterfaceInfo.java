package com.kevinhuang.simplerpc.common;

/**
 * The type Service info.
 */
public class ServiceInterfaceInfo {

    public ServiceInterfaceInfo(String serviceName, String ip, Integer port, Class<?> clazz, Object obj) {
        this.serviceName = serviceName;
        this.ip = ip;
        this.port = port;
        this.clazz = clazz;
        this.obj = obj;
    }
    private String serviceName;

    private String ip;

    private  Integer port;

    private  Class<?> clazz;

    private Object obj;

    /**
     * Gets service name.
     *
     * @return the service name
     */
    public String getServiceName() {
        return serviceName;
    }
    /**
     * Sets service name.
     *
     * @param serviceName the service name
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    /**
     * Gets ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }
    /**
     * Sets ip.
     *
     * @param ip the ip
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
    /**
     * Gets port.
     *
     * @return the port
     */
    public Integer getPort() {
        return port;
    }
    /**
     * Sets port.
     *
     * @param port the port
     */
    public void setPort(Integer port) {
        this.port = port;
    }
    /**
     * Gets clazz.
     *
     * @return the clazz
     */
    public Class<?> getClazz() {
        return clazz;
    }
    /**
     * Sets clazz.
     *
     * @param clazz the clazz
     */
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
    /**
     * Gets obj.
     *
     * @return the obj
     */
    public Object getObj() {
        return obj;
    }
    /**
     * Sets obj.
     *
     * @param obj the obj
     */
    public void setObj(Object obj) {
        this.obj = obj;
    }
}
