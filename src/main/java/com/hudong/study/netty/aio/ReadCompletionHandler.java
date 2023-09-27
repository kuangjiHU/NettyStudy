package com.hudong.study.netty.aio;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/9/27 23:01
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

        private AsynchronousSocketChannel socketChannel;

        public ReadCompletionHandler(AsynchronousSocketChannel socketChannel) {
            if (this.socketChannel == null) {
                this.socketChannel = socketChannel;
            }
        }

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            attachment.flip();
            byte[] body = new byte[attachment.remaining()];
            attachment.get(body);
            String req = new String(body, StandardCharsets.UTF_8);
            System.out.println("The time server receive order order :" + req);
            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
            doWrite(currentTime);
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            try {
                this.socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void doWrite(String currentTime) {
            if (StringUtils.isNotEmpty(currentTime)) {
                byte[] bytes = currentTime.getBytes(StandardCharsets.UTF_8);
                ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
                byteBuffer.put(bytes);
                byteBuffer.flip();
                socketChannel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        System.out.println("send done");
                        if (attachment.hasRemaining()) {
                            socketChannel.write(attachment, attachment, this);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        try {
                            socketChannel.close();
                        } catch (IOException e) {
                            //
                        }
                    }
                });
            }
        }
}
