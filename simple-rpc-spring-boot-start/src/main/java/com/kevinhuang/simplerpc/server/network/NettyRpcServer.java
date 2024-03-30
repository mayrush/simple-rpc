package com.kevinhuang.simplerpc.server.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServer implements RpcServer {

    private int port;

    private RequestHandler requestHandler;

    private Channel channel;


    public NettyRpcServer(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }


    @Override
    public void start() {
        // 创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务端端启动对象
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    // 设置两个线程组
                    .group(bossGroup, workerGroup)
                    // 设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)
                    // 服务端用于接受进来端连接，也就是bossGroup线程，线程队列大小
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // child 通道 worker 线程处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 给pipeline 管道设定自定义端处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ChannelRequestHandler());
                        }
                    });
            // 绑定端口号，同步启动服务
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("[sample-rpc]RPC Server started on port:{}", port);
            channel = channelFuture.channel();
            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("Server error.", e);
        } finally {
            // 释放线程组资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
    @Override
    public void stop() {
        channel.close();
    }

    private class ChannelRequestHandler extends ChannelInboundHandlerAdapter {


        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("Server receive a message : {}", msg);
            final ByteBuf msgBuf = (ByteBuf) msg;
            final byte[] reqBytes = new byte[msgBuf.readableBytes()];
            msgBuf.readBytes(reqBytes);
            // 调用请求处理器 开始处理客户端请求
            final byte[] respBytes =requestHandler.handleRequest(reqBytes);
            log.info("Send response message:{}",respBytes);
            final ByteBuf resBuf = Unpooled.buffer(respBytes.length);
            resBuf.writeBytes(respBytes);
            ctx.writeAndFlush(resBuf);
        }
    }

}
