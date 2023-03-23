package com.zust.rpc.client.proxy;

import com.zust.rpc.client.config.RpcClientProperties;
import com.zust.rpc.core.discovery.DiscoveryService;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态代理
 * 应用于 RpcClientProcessor
 */
public class ClientStubProxyFactory {

    private Map<Class<?>, Object> objectCache = new HashMap<>();

    /**
     * 获取代理对象
     *
     * @param clazz   接口
     * @param version 服务版本
     * @param <T>
     * @return 代理对象
     */
    public <T> T getProxy(Class<T> clazz,
                          String version,
                          DiscoveryService discoveryService,
                          RpcClientProperties properties) {
        /*
           Map 的 computeIfAbsent() 如果不存在这个 key，则添加到 hashMap 中。
           如果存在, 则返回对应 value
         */
        return (T) objectCache.computeIfAbsent(clazz, clz ->
                Proxy.newProxyInstance(clz.getClassLoader(),
                        new Class[]{clz},
                        new ClientStubInvocationHandler(
                                discoveryService,
                                properties,
                                clz, version))
        );
    }
}
