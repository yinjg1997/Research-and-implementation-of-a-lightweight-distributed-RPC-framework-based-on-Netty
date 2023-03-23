package com.zust.rpc.server.config;

import com.zust.rpc.core.register.RegistryService;
import com.zust.rpc.core.register.ZookeeperRegistryService;
import com.zust.rpc.server.RpcServerProvider;
import com.zust.rpc.server.transport.NettyRpcServer;
import com.zust.rpc.server.transport.RpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname RpcServerConfiguration
 * @Description rpc server 配置
 */
@Configuration
@EnableConfigurationProperties(RpcServerProperties.class)
public class RpcServerAutoConfiguration {

    @Autowired
    private RpcServerProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public RegistryService registryService() {
        return new ZookeeperRegistryService(properties.getRegistryAddr());
    }

    @Bean
    @ConditionalOnMissingBean(RpcServer.class)
    RpcServer RpcServer() {
        return new NettyRpcServer();
    }

    @Bean
    @ConditionalOnMissingBean(RpcServerProvider.class)
    RpcServerProvider rpcServerProvider(@Autowired RegistryService registryService,
                                        @Autowired RpcServer rpcServer,
                                        @Autowired RpcServerProperties rpcServerProperties){
        return new RpcServerProvider(registryService, rpcServer, rpcServerProperties);
    }
}
