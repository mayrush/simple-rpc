package com.kevinhuang.simplerpc.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DefaultMessageProtocol implements MessageProtocol {


    @Override
    public RpcRequest unmarshallingReqMessage(byte[] reqBytes) throws Exception {
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(reqBytes));
        return (RpcRequest) inputStream.readObject();
    }


    @Override
    public byte[] marshallingRespMessage(RpcResponse response) throws Exception {
        return serialize(response);
    }


    @Override
    public RpcResponse unmarshallingRespMessage(byte[] respBytes) throws Exception {
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(respBytes));
        return (RpcResponse) inputStream.readObject();
    }


    @Override
    public byte[] marshallingReqMessage(RpcRequest request) throws Exception {
        return serialize(request);
    }


    private byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outputStream);
        out.writeObject(obj);
        return outputStream.toByteArray();
    }


}
