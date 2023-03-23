package com.zust.rpc.provider.service;

import com.zust.rpc.api.service.HelloWordService;
import com.zust.rpc.server.annotation.RpcService;

/**
 * @Classname HelloWordServiceImpl
 */
@RpcService(interfaceType = HelloWordService.class, version = "1.0")
public class HelloWordServiceImpl implements HelloWordService {

    @Override
    public String sayHello(String name) {
        return String.format("您好：%s, rpc 调用成功", name);
    }

}
