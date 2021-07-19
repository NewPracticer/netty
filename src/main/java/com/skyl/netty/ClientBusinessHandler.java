package com.skyl.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import sun.security.pkcs11.wrapper.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.*;

@ChannelHandler.Sharable
public class ClientBusinessHandler extends SimpleChannelInboundHandler<MyProtocolMessage> {

    public static final ChannelHandler INSTANCE = new ClientBusinessHandler();
    //未考虑原子性，只做demo演示
    int i = 0;
    static ExecutorService executorService = new ThreadPoolExecutor(1, 3, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(1000), new ThreadPoolExecutor.DiscardOldestPolicy());
    //定时器线程池
    static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    CharSequence machinNum = "9380SL1911130005";

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //设置channle 高水位值
         ctx.channel().config().setWriteBufferHighWaterMark(20 * 1024 * 1024);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
                Date date = new Date();// 获取当前时间
                if (ctx.channel().isWritable()) {
                    int version = 1;
                    String name = UUID.randomUUID().toString();
                    String content = "客户端发送的心跳";
                    int type = 10;

                    MyProtocolHeader header = new MyProtocolHeader(version,machinNum, type);
                    MyProtocolMessage msg = new MyProtocolMessage(header, content);
                    ctx.channel().writeAndFlush(msg);
                    System.out.println("发送数据到服务器, type:"+type);
                } else {

                }
            }
        },0, 10, TimeUnit.MILLISECONDS);
//        ctx.executor().scheduleAtFixedRate(() -> {
//
//        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyProtocolMessage msg) {
        System.out.println("读取服务器数据, msg类型:"+msg.getMyProtocolHeader().getMsgType());
        //收到的数据类型可以整除50 进入客户端阻塞状态
        if (msg.getMyProtocolHeader().getMsgType() % 50 == 0) {
            //客户端阻塞演示
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000 * 40);
                        int version = 1;
                        String name = UUID.randomUUID().toString();
                        String content = Constant.text;
                        int type = 9999;
                        //客户端发送数据长度与服务器接收数据长度进行对比
                        MyProtocolHeader header = new MyProtocolHeader(version,machinNum, type);
                        MyProtocolMessage anothermsg = new MyProtocolMessage(header, content);
                        ctx.channel().writeAndFlush(anothermsg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // ignore
    }



    ;


}