package com.hudong.study.netty.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/9/27 23:03
 */
public class AsyncTimeServerHandler implements Runnable {

    private int port;

    private CountDownLatch latch;

    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServerHandler(int port) {
        this.port = port;
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        this.doAccept();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doAccept() {
        System.out.println("start time server");
        asynchronousServerSocketChannel.accept(this,
                new CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler>() {

                    @Override
                    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
                        //当连接成功了之后，再把当前处理器绑定到管道当中，处理接下来的事件
                        attachment.asynchronousServerSocketChannel.accept(attachment, this);
                        System.out.println("listening");
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        result.read(buffer, buffer, new ReadCompletionHandler(result));
                    }

                    @Override
                    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
                        attachment.latch.countDown();
                    }
                });


    }

}
