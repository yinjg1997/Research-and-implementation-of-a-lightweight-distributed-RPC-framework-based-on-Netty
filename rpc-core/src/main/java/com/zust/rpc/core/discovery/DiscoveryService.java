package com.zust.rpc.core.discovery;

import com.zust.rpc.core.common.ServiceInfo;

/**
 * @Classname DiscoveryService
 * @Description
 */
public interface DiscoveryService {

    /**
     *  发现
     * @param serviceName
     * @return
     * @throws Exception
     */
    ServiceInfo discovery(String serviceName) throws Exception;

}
