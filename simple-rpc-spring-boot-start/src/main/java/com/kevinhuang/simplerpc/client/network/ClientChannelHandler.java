package com.kevinhuang.simplerpc.client.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private byte[] data;

    private byte[] response;

    private CountDownLatch countDownLatch;

    public ClientChannelHandler(byte[] data) {
        this.data = data;
        countDownLatch = new CountDownLatch(1);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 通道激活后客户端开始发送数据
        final ByteBuf buf = Unpooled.buffer(data.length);
        buf.writeBytes(data);
        log.info("Client start send message: {}", data);
        ctx.writeAndFlush(buf);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("Client received message: {}", msg);
        ByteBuf buf = (ByteBuf) msg;
        response = new byte[buf.readableBytes()];
        buf.readBytes(response);
        countDownLatch.countDown();
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Caught exception: {}", cause);
        ctx.close();
    }

    public byte[] response() {
        try {
            countDownLatch.await();
        } catch (Exception e) {
            log.error("Client count error: {}", e);
        }
        return response;
    }



}
