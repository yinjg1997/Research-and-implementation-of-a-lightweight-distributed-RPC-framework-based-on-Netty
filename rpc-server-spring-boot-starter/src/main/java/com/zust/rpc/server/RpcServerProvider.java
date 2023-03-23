package com.zust.rpc.server;

import com.zust.rpc.core.common.ServiceInfo;
import com.zust.rpc.core.common.ServiceUtil;
import com.zust.rpc.core.register.RegistryService;
import com.zust.rpc.server.annotation.RpcService;
import com.zust.rpc.server.config.RpcServerProperties;
import com.zust.rpc.server.store.LocalServerCache;
import com.zust.rpc.server.transport.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;

import java.net.InetAddress;

/**
 * @Classname RpcServerProvider
 * @Description
 */
@Slf4j
public class RpcServerProvider implements BeanPostProcessor, CommandLineRunner {

    private RegistryService registryService;

    private RpcServerProperties properties;

    private RpcServer rpcServer;

    public RpcServerProvider(RegistryService registryService, RpcServer rpcServer, RpcServerProperties properties) {
        this.registryService = registryService;
        this.properties = properties;
        this.rpcServer = rpcServer;
    }


    /**
     * 所有bean 实例化之后处理
     * <p>
     * 暴露服务注册到注册中心
     * <p>
     * 容器启动后开启netty服务处理请求
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        if (rpcService != null) {
            try {
                String serviceName = rpcService.interfaceType().getName();
                String version = rpcService.version();
                /*
                 * 将暴露的服务缓存到本地
                 * 在处理 RPC 请求时可以直接通过 cache 拿到对应的服务进行调用。避免反射实例化服务开销
                 */
                LocalServerCache.store(ServiceUtil.serviceKey(serviceName, version), bean);

                /*
                   设置服务信息
                 */
                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setServiceName(ServiceUtil.serviceKey(serviceName, version));
                serviceInfo.setPort(properties.getPort());
                serviceInfo.setAddress(InetAddress.getLocalHost().getHostAddress());
                serviceInfo.setAppName(properties.getAppName());

                /*
                    将服务信息 设置到 注册中心, 用于服务发现
                    服务注册
                 */
                registryService.register(serviceInfo);
            } catch (Exception ex) {
                log.error("服务注册出错:{}", ex);
            }
        }
        return bean;
    }

    /**
     * 启动rpc服务 处理请求
     * 这个 run 是随 Springboot 启动的
     * @param args
     */
    @Override
    public void run(String... args) {
        /*
            这里默认启动的就是 Netty, 是在自动配置中设置的
         */
        new Thread(() -> rpcServer.start(properties.getPort())).start();
        log.info(" rpc server :{} start, appName :{} , port :{}", rpcServer, properties.getAppName(), properties.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // 关闭之后把服务从ZK上清除
                registryService.destroy();
            }catch (Exception ex){
                log.error("", ex);
            }

        }));
    }

}
