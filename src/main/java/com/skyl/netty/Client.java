package com.skyl.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import java.util.concurrent.TimeUnit;


public class Client {


    private static final String SERVER_HOST = "127.0.0.1";
    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    EventLoopGroup businessGroup = new NioEventLoopGroup(10);
    final Bootstrap bootstrap = new Bootstrap();

    public static void main(String[] args) throws Exception {
        new Client().start(Constant.PORT);
    }

    public void start(int port) throws Exception {

        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        //ByteBuf内存分配方式
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ByteBuf delimiter = Unpooled.copiedBuffer("\r\n\t\b\f".getBytes());
                ch.pipeline().addLast("framer", new DelimiterBasedFrameDecoder(1024* 1024, delimiter));
                ch.pipeline().addLast(new MyEncoder());
                ch.pipeline().addLast(new MyDecoder());
                ch.pipeline().addLast(businessGroup, ClientBusinessHandler.INSTANCE);
            }
        });

        doconnect();
    }

    public void doconnect() {
        ChannelFuture channelFuture = bootstrap.connect(SERVER_HOST, Constant.PORT);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {

                } else {
                    channelFuture.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            bootstrap.connect(SERVER_HOST, Constant.PORT);
                        }
                    }, 10, TimeUnit.SECONDS);
                }

            }
        });
    }


}
