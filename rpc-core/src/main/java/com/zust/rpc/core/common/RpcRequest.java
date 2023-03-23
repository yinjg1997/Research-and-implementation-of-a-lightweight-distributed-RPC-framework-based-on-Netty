package com.zust.rpc.core.common;

import lombok.Data;

import java.io.Serializable;


/**
 * 数据封装对象 RpcRequest,
 * 应用于 MessageProtocol
 *
 */
@Data
public class RpcRequest implements Serializable {

    /**
     * 请求的服务名 + 版本
     * 或者说 请求的 接口名 + 版本
     */
    private String serviceName;
    /**
     * 请求调用的方法
     */
    private String method;

    /**
     *  参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     *  参数
     */
    private Object[] parameters;
}
