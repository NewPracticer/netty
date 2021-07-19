package com.skyl.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;



public class Server {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup businessGroup = new NioEventLoopGroup(2000);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);


        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ByteBuf delimiter = Unpooled.copiedBuffer("\r\n\t\b\f".getBytes());
                ch.pipeline().addLast("framer", new DelimiterBasedFrameDecoder(20*1024*1024,delimiter));
                ch.pipeline().addLast(new MyEncoder());
                ch.pipeline().addLast(new MyDecoder());
                // 针对客户端，如果在1分钟时没有向服务端发送读写心跳(ALL)，则主动断开,如果是读空闲或者写空闲，不处理
                ch.pipeline().addLast(new IdleStateHandler(40, 50, 55));
                // 自定义的空闲状态检测
                ch.pipeline().addLast(new HeartBeatHandler());
                ch.pipeline().addLast(businessGroup, ServerBusinessHandler.INSTANCE);
            }
        });

        bootstrap.bind(Constant.PORT).addListener((ChannelFutureListener) future -> System.out.println("bind success in port: " + Constant.PORT));

    }

}
