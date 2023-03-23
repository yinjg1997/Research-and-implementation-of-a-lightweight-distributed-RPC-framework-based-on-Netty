package com.zust.rpc.client.proxy;

import com.zust.rpc.client.config.RpcClientProperties;
import com.zust.rpc.client.transport.NetClientTransportFactory;
import com.zust.rpc.client.transport.RequestMetadata;
import com.zust.rpc.core.common.RpcRequest;
import com.zust.rpc.core.common.RpcResponse;
import com.zust.rpc.core.common.ServiceInfo;
import com.zust.rpc.core.common.ServiceUtil;
import com.zust.rpc.core.discovery.DiscoveryService;
import com.zust.rpc.core.exception.ResourceNotFoundException;
import com.zust.rpc.core.exception.RpcException;
import com.zust.rpc.core.protocol.MessageHeader;
import com.zust.rpc.core.protocol.MessageProtocol;
import com.zust.rpc.core.protocol.MsgStatus;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * 调用程序处理器
 */
@Slf4j
public class ClientStubInvocationHandler implements InvocationHandler {

    private DiscoveryService discoveryService;

    private RpcClientProperties properties;

    private Class<?> calzz;

    private String version;

    public ClientStubInvocationHandler(DiscoveryService discoveryService, RpcClientProperties properties, Class<?> calzz, String version) {
        super();
        this.calzz = calzz;
        this.version = version;
        this.discoveryService = discoveryService;
        this.properties = properties;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /*
            1、获得服务信息
            从 RPC core 中 查找当前服务是否被注册, 如果没有被注册,则抛404

            如果已被注册,则从zk中获取服务信息,获得服务信息存储了如何访问到服务的信息, 服务的host,port,appName
         */
        ServiceInfo serviceInfo = discoveryService.discovery(ServiceUtil.serviceKey(this.calzz.getName(), this.version));
        if (serviceInfo == null) {
            throw new ResourceNotFoundException("404");
        }

        /**
         * 获得 RPC 协议
         */
        MessageProtocol<RpcRequest> messageProtocol = new MessageProtocol<>();
        // 设置 RPC请求头, 里面定义了序列化方式
        messageProtocol.setHeader(MessageHeader.build(properties.getSerialization()));
        // 设置 RPC请求体, RpcRequest 具体的请求接口信息和参数
        RpcRequest request = new RpcRequest();
        request.setServiceName(ServiceUtil.serviceKey(this.calzz.getName(), this.version));
        request.setMethod(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        messageProtocol.setBody(request);

        /**
         * Transfer, 进行网络传输, 这里使用 Netty
         */
        // 发送网络请求 拿到结果
        MessageProtocol<RpcResponse> responseMessageProtocol = NetClientTransportFactory.getNetClientTransport()
                .sendRequest(RequestMetadata.builder().protocol(messageProtocol).address(serviceInfo.getAddress())
                        .port(serviceInfo.getPort()).timeout(properties.getTimeout()).build());

        if (responseMessageProtocol == null) {
            log.error("请求超时");
            throw new RpcException("rpc调用结果失败， 请求超时 timeout:" + properties.getTimeout());
        }

        if (!MsgStatus.isSuccess(responseMessageProtocol.getHeader().getStatus())) {
            log.error("rpc调用结果失败， message：{}", responseMessageProtocol.getBody().getMessage());
            throw new RpcException(responseMessageProtocol.getBody().getMessage());
        }
        return responseMessageProtocol.getBody().getData();
    }
}
