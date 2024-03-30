package com.kevinhuang.simplerpc.client.network;

import com.kevinhuang.simplerpc.common.ServiceInterfaceInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * The type Netty rpc client.
 */
public class NettyRpcClient implements RpcClient {


    @Override
    public byte[] sendMessage(byte[] data, ServiceInterfaceInfo serviceInterfaceInfo) throws InterruptedException {
        final String ip = serviceInterfaceInfo.getIp();
        final Integer port = serviceInterfaceInfo.getPort();

        ClientChannelHandler clientChannelHandler = new ClientChannelHandler(data);

        // 初始化 netty 客户端
        final Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    // 初始化通道，并在通道流水线中注册通道处理器
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        final ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(clientChannelHandler);
                    }
                });

        // 连接服务端并发送消息
        bootstrap.connect(ip,port).sync();

        return clientChannelHandler.response();
    }
}
