package com.hudong.study.netty.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/9/27 22:49
 */
public class AioTimeClient {

    public static void main(String[] args) throws InterruptedException {
        int port = 8129;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port)).start();
        Thread.currentThread().join();
    }

    public static class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {

        private AsynchronousSocketChannel client;

        private String host;

        private int port;

        private CountDownLatch latch;

        public AsyncTimeClientHandler(String host, int port) {
            this.host = host;
            this.port = port;
            try {
                client = AsynchronousSocketChannel.open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            latch = new CountDownLatch(1);
            client.connect(new InetSocketAddress(host, port), this, this);
            try {
                latch.await();
                System.out.println("连接关闭。。。。。");
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void completed(Void result, AsyncTimeClientHandler attachment) {
            System.out.println("send require");
            byte[] req = "QUERY TIME ORDER".getBytes(StandardCharsets.UTF_8);
            ByteBuffer byteBuffer = ByteBuffer.allocate(req.length);
            byteBuffer.put(req);
            byteBuffer.flip();
            client.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if (attachment.hasRemaining()) {
                        client.write(attachment, attachment, this);
                    } else {
                        ByteBuffer read = ByteBuffer.allocate(1024);
                        client.read(read, read, new CompletionHandler<Integer, ByteBuffer>() {
                            @Override
                            public void completed(Integer result, ByteBuffer attachment) {
                                attachment.flip();
                                byte[] bytes = new byte[attachment.remaining()];
                                attachment.get(bytes);
                                String body = new String(bytes, StandardCharsets.UTF_8);
                                System.out.println("Now is :" + body);
                                latch.countDown();
                            }

                            @Override
                            public void failed(Throwable exc, ByteBuffer attachment) {
                                try {
                                    client.close();
                                    latch.countDown();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        client.close();
                        latch.countDown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
            try {
                client.close();
                latch.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
