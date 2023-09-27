package com.hudong.study.netty.nio;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/9/12 23:51
 */
public class TimeClientHandle implements Runnable {

    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public TimeClientHandle(String ip, int port) {
        this.host = StringUtils.isEmpty(ip) ? "127.0.0.1" : ip;
        this.port = port;
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        // TODO to connect
        try {
            if (socketChannel.connect(new InetSocketAddress(host, port))) {
                socketChannel.register(selector, SelectionKey.OP_READ);
                doWrite(socketChannel);
            } else {
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        while (!stop) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    // handle input key
                    if (key.isValid()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        if (key.isConnectable()) {
                            if (sc.finishConnect()) {
                                sc.register(selector, SelectionKey.OP_READ);
                                doWrite(sc);
                            } else {
                                System.exit(1);
                            }
                        }
                        if (key.isReadable()) {
                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                            int readBytes = sc.read(readBuffer);
                            if (readBytes > 0) {
                                readBuffer.flip();
                                byte[] bytes = new byte[readBuffer.remaining()];
                                readBuffer.get(bytes);
                                String body = new String(bytes, Charset.defaultCharset());
                                System.out.println("Now is : " + body);
                                this.stop = true;
                            } else if (readBytes < 0) {
                                key.cancel();
                                sc.close();
                            } else {
                                ;
                            }
                        }
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void doWrite(SocketChannel sc) throws IOException {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        sc.write(writeBuffer);
        if (!writeBuffer.hasRemaining()) {
            System.out.println("Send order 2 server succeed.");
        }
    }


}
