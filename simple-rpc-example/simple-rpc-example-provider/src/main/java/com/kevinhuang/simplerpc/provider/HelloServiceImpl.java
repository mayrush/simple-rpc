package com.kevinhuang.simplerpc.provider;

import com.kevinhuang.simplerpc.annotation.ServiceExpose;
import com.kevinhuang.simplerpc.provider.api.HelloService;

@ServiceExpose
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "「来自kevin的问候」：hello " + name + "，恭喜你学会了RPC造轮子!";
    }
}
