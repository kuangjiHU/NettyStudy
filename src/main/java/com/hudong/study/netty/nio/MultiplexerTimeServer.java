package com.hudong.study.netty.nio;


import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/9/5 0:01
 */
public class MultiplexerTimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel servChannel;
    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port), 1024);
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                selector.select(); // 1s
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    if (key.isValid()) {
                        if (key.isAcceptable()) {
                            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                            // 完成三次握手
                            SocketChannel client = ssc.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);

                        }
                        handleInput(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    selector.close();
                } catch (IOException ex) {

                }
            }
        }
    }

    private static void handleInput(SelectionKey key) throws IOException {
        if (key.isReadable()) {
            try (SocketChannel client = (SocketChannel) key.channel()) {
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = client.read(readBuffer);
                if (readBytes > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, StandardCharsets.UTF_8);
                    System.out.println("The time server receive order : " + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                    doWrite(client, currentTime);
                } else if (readBytes < 0) {
                    // 链路已经关闭
                    key.cancel();
                    client.close();
                } else {
                    ;
                }
            }
        }
    }

    private static void doWrite(SocketChannel client, String response) throws IOException {
        if (StringUtils.isNotEmpty(response)) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            client.write(writeBuffer);
        }
    }
}
