package com.zust.rpc.core.register;

import com.zust.rpc.core.common.ServiceInfo;

import java.io.IOException;

/**
 * @Classname RegistryService
 * @Description 服务注册发现
 */
public interface RegistryService {

    void register(ServiceInfo serviceInfo) throws Exception;

    void unRegister(ServiceInfo serviceInfo) throws Exception;

    void destroy() throws IOException;

}
