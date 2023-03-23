package com.zust.rpc.client.transport;

import com.zust.rpc.core.common.RpcResponse;
import com.zust.rpc.core.protocol.MessageProtocol;

/**
 * @Classname NetClientTransport
 * @Description 网络传输层
 */
public interface NetClientTransport {

    /**
     *  发送数据
     * @param metadata
     * @return
     * @throws Exception
     */
    MessageProtocol<RpcResponse> sendRequest(RequestMetadata metadata) throws Exception;

}
