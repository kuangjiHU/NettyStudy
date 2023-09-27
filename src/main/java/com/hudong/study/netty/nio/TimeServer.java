package com.hudong.study.netty.nio;


/**
 * @author Administrator
 * @version 1.0
 * @date 2023/8/30 22:18
 */

public class TimeServer {
    public static void main(String[] args) {
        int port = 21291;
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-multiplexerTimeServer-001").start();
    }
}
