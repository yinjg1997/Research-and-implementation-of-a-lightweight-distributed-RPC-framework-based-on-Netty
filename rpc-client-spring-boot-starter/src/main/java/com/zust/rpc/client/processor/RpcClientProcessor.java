package com.zust.rpc.client.processor;

import com.zust.rpc.client.annotation.RpcAutowired;
import com.zust.rpc.client.config.RpcClientProperties;
import com.zust.rpc.client.proxy.ClientStubProxyFactory;
import com.zust.rpc.core.discovery.DiscoveryService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 *
 * 进行远程调用
 *
 *
 * @Classname RpcClientProcessor
 * @Description bean 后置处理器 获取所有bean
 * 判断bean字段是否被 {@link RpcAutowired } 注解修饰
 * 动态修改被修饰字段的值为代理对象 {@link ClientStubProxyFactory}
 */
public class RpcClientProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    /*
        解耦, 就是为了获取代理对象
     */
    private ClientStubProxyFactory clientStubProxyFactory;

    /*
        服务发现
     */
    private DiscoveryService discoveryService;

    /*
        配置
     */
    private RpcClientProperties properties;

    /*
        用于获取 bean
     */
    private ApplicationContext applicationContext;

    public RpcClientProcessor(ClientStubProxyFactory clientStubProxyFactory, DiscoveryService discoveryService, RpcClientProperties properties) {
        this.clientStubProxyFactory = clientStubProxyFactory;
        this.discoveryService = discoveryService;
        this.properties = properties;
    }

    /**
     * postProcessBeanFactory() 当所有类加载后,执行
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.getClass().getClassLoader());
                ReflectionUtils.doWithFields(clazz, field -> {
                    // 如果该类的字段上有 RpcAutowired 注解, 就获得该 字段 的代理对象
                    RpcAutowired rpcAutowired = AnnotationUtils.getAnnotation(field, RpcAutowired.class);
                    if (rpcAutowired != null) {
                        Object bean = applicationContext.getBean(clazz);
                        field.setAccessible(true);
                        // 修改为代理对象
                        ReflectionUtils.setField(field, bean,
                                clientStubProxyFactory.getProxy(field.getType(), rpcAutowired.version(), discoveryService, properties));
                    }
                });
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
