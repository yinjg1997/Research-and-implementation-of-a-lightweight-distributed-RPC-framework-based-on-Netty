package com.zust.rpc.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Classname RpcClientProperties
 */
@Data
public class RpcClientProperties {

    /**
     *  负载均衡
     */
    private String balance;

    /**
     *  序列化
     */
    private String serialization;

    /**
     *  服务发现地址, 这是 zookeeper 默认端口
     */
    private String discoveryAddr = "127.0.0.1:2181";

    /**
     *  服务调用超时
     */
    private Integer timeout;

}
