package com.hudong.study.netty.nettynio;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/10/7 22:28
 */


@Slf4j
public class TimeClient {
    private final AtomicInteger clientNO = new AtomicInteger();
    private final Bootstrap b = new Bootstrap();
    NioEventLoopGroup group = new NioEventLoopGroup();
    ChannelFuture f =null;
    final static ExecutorService workers = new ThreadPoolExecutor(4, 12, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000));

    public TimeClient(int clientNO) {
        this.clientNO.set(clientNO);
    }

    public void connect(int port, String host) throws Exception {

        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new TimeClientHandler());
                    }
                });
        try {
            f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws Exception {

        new TimeClient(1).connect(8131, "localhost");
    }
}
