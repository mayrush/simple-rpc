package com.kevinhuang.simplerpc.client.network;

import com.kevinhuang.simplerpc.common.ServiceInterfaceInfo;

public interface RpcClient {

    byte[] sendMessage(byte[] data, ServiceInterfaceInfo serviceInterfaceInfo) throws InterruptedException;
}
