package com.skyl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 解码器，将入站的数据从一种格式转换成另外一种格式
 */
public class MyDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        // 获取协议的版本
        int version = in.readInt();
        // 获取设备编码
        CharSequence machineNum = in.readCharSequence(16,StandardCharsets.UTF_8);
        //获取消息类型
        int msgType = in.readInt();
        // 组装协议头
        MyProtocolHeader header = new MyProtocolHeader(version,machineNum,msgType);
        // 读取消息内容
        ByteBuf buf = in.readBytes(in.readableBytes());
        byte[] content = new byte[buf.readableBytes()];
        buf.readBytes(content);

        MyProtocolMessage message = new MyProtocolMessage(header, new String(content));
        out.add(message);
        if(buf.refCnt() >0 ){
            buf.release();
        }
    }
}
