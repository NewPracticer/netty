package com.skyl.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@ChannelHandler.Sharable
public class ServerBusinessHandler extends SimpleChannelInboundHandler<MyProtocolMessage> {
    public static final ChannelHandler INSTANCE = new ServerBusinessHandler();

    // 用于记录和管理所有客户端的channle
    public static ChannelGroup users =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    int i =0;
    CharSequence machinNum = "9380SL1911130000";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyProtocolMessage msg) {
        System.out.println("服务器读取到数据, msg类型:"+msg.getMyProtocolHeader().getMsgType());
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        if(msg.getMyProtocolHeader().getMsgType() == 9999){
//            ctx.channel().writeAndFlush(msg);
            System.out.println("收到客户端回传数据 数据内容:"+msg.getContent());
        }
        else if(msg.getMyProtocolHeader().getMsgType() == 10){
            int version = 1;
            String name = UUID.randomUUID().toString();
            String content = "服务器收到的心跳";
            int type = i++;
            MyProtocolHeader header = new MyProtocolHeader(version,machinNum, type);
            MyProtocolMessage firstMsg = new MyProtocolMessage(header, content);
            ctx.channel().writeAndFlush(firstMsg);
            System.out.println("服务器发送数据, msg类型:"+msg.getMyProtocolHeader().getMsgType());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // ignore
        cause.printStackTrace();
        // 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
        ctx.channel().close();
        users.remove(ctx.channel());
    }

    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channle，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().remoteAddress();
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        String channelId = ctx.channel().id().asShortText();

        users.remove(ctx.channel());
    }
}
