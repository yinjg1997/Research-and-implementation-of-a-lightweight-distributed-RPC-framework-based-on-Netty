package com.zust.rpc.server.store;


import java.util.HashMap;
import java.util.Map;

/**
 * @Classname LocalServerCache
 * @Description 将暴露的服务缓存到本地
 * 在处理 RPC 请求时可以直接通过 cache 拿到对应的服务进行调用。避免反射实例化服务开销
 *
 *
 */
public final class LocalServerCache {

    private static final Map<String, Object> serverCacheMap = new HashMap<>();

    // 应用于服务提供者的注册过程中
    public static void store(String serverName, Object server) {
        serverCacheMap.merge(serverName, server, (Object oldObj, Object newObj) -> newObj);
    }

    // 应用于服务 RpcRequestHandler
    public static Object get(String serverName) {
        return serverCacheMap.get(serverName);
    }

    public static Map<String, Object> getAll(){
        return null;
    }
}
