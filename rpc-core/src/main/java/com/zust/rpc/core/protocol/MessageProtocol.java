package com.zust.rpc.core.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @Classname MessageProtocol
 * @Description 消息协议
 * 应用于 RequestMetadata
 */
@Data
public class MessageProtocol<T> implements Serializable {

    /**
     *  消息头
     */
    private MessageHeader header;

    /**
     *  消息体
     */
    private T body;

}
