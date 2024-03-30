package com.kevinhuang.simplerpc.consumer;

import com.kevinhuang.simplerpc.annotation.ServiceReference;
import com.kevinhuang.simplerpc.provider.api.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {

    @ServiceReference
    private HelloService helloService;


    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        final String rsp = helloService.sayHello(name);
        log.info("Receive message from rpc server, msg: {}", rsp);
        return rsp;
    }



}
