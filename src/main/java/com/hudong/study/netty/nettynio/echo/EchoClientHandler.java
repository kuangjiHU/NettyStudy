package com.hudong.study.netty.nettynio.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/10/7 22:38
 */

@Slf4j
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    private int counter;
    private static final String ECHO_REQ = "Hi, HKJ. Welcome to Netty.$_";

    public EchoClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String body = (String) msg;
        System.out.println("Now is " + ++counter   + " times receive from server: " + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("unexpected exception from downstream : " + cause.getMessage());
        ctx.close();
    }
}
