package com.skyl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 *  编码器，将出站的数据从一种格式转换成另外一种格式。
 */
public class MyEncoder extends MessageToByteEncoder<MyProtocolMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MyProtocolMessage message, ByteBuf out) throws Exception {

        // 将Message转换成二进制数据
        MyProtocolHeader header = message.getMyProtocolHeader();
        // 这里写入的顺序就是协议的顺序.
        // 写入Header信息

        out.writeInt(header.getVersion());
        out.writeCharSequence(header.getMachineNum(), StandardCharsets.UTF_8);
        out.writeInt(header.getMsgType());
        // 写入消息主体信息
        out.writeBytes(message.getContent().getBytes());
        out.writeBytes(new byte[]{'\r','\n','\t','\b','\f'});

    }

}
