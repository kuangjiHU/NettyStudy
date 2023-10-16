package com.hudong.study.netty.nettynio.time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/10/7 22:38
 */

@Slf4j
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private int counter;
    private byte[] req;


    public TimeClientHandler() {
        req = ("QUERY TIME ORDER" + System.lineSeparator()).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf msg = null;
        for (int i = 0; i < 100; i++) {
            msg = Unpooled.buffer(req.length);
            msg.writeBytes(req);
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String body = (String) msg;
        System.out.println("Now is " + ++counter   + " : " + body);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("unexpected exception from downstream : " + cause.getMessage());
        ctx.close();
    }
}
