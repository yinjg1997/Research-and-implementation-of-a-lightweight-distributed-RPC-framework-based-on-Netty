package com.zust.rpc.core.discovery;

import com.zust.rpc.core.balancer.LoadBalance;
import com.zust.rpc.core.common.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @Classname ZookeeperRegistryService
 */
@Slf4j
public class ZookeeperDiscoveryService implements DiscoveryService {

    public static final int BASE_SLEEP_TIME_MS = 1000;
    public static final int MAX_RETRIES = 3;
    public static final String ZK_BASE_PATH = "/demo_rpc";

    private ServiceDiscovery<ServiceInfo> serviceDiscovery;

    private LoadBalance loadBalance;

    /**
     * 系统启动运行 服务发现, 进行服务发现 serviceDiscovery 的初始化
     * @param registryAddr
     * @param loadBalance
     */
    public ZookeeperDiscoveryService(String registryAddr, LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
        try {
            CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddr, new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
            client.start();
            JsonInstanceSerializer<ServiceInfo> serializer = new JsonInstanceSerializer<>(ServiceInfo.class);
            this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceInfo.class)
                    .client(client)
                    .serializer(serializer)
                    .basePath(ZK_BASE_PATH)
                    .build();
            this.serviceDiscovery.start();
        } catch (Exception e) {
            log.error("serviceDiscovery start error :{}", e);
        }
    }


    /**
     *  使用zk进行服务发现和负载均衡
     *
     *  应用于 ClientStubInvocationHandler
     * @param serviceName
     * @return
     * @throws Exception
     */
    @Override
    public ServiceInfo discovery(String serviceName) throws Exception {
        // queryForInstances 由 zk 完成
        Collection<ServiceInstance<ServiceInfo>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        return CollectionUtils.isEmpty(serviceInstances) ? null
                : loadBalance.chooseOne(serviceInstances
                .stream().map(ServiceInstance::getPayload)
                .collect(Collectors.toList()));
    }

}
