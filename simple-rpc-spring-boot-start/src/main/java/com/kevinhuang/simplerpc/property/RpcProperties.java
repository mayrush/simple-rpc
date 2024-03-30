package com.kevinhuang.simplerpc.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "kevinhuang.simple.rpc")
@Data
public class RpcProperties {

    private int exposePort = 6666;

    private String registerAddress;

    private String register;

    private String protocol = "java";

}
