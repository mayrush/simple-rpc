package com.kevinhuang.simplerpc.serialization;

/**
 * The interface Message protocol.
 */
public interface MessageProtocol {


    /**
     * Unmarshalling req message rpc request.
     *
     * @param reqBytes the req bytes
     * @return the rpc request
     */
    RpcRequest unmarshallingReqMessage(byte[] reqBytes) throws Exception;


    /**
     * Marshalling resp message byte [ ].
     *
     * @param response the response
     * @return the byte [ ]
     */
    byte[] marshallingRespMessage(RpcResponse response) throws Exception;


    RpcResponse unmarshallingRespMessage(byte[] respBytes) throws Exception;



    byte[] marshallingReqMessage(RpcRequest request) throws Exception;
}
