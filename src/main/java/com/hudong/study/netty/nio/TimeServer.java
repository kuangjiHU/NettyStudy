package com.hudong.study.netty.nio;

import com.hudong.study.netty.bio.TimeServerHandler;
import com.hudong.study.netty.bio.TimeServerHandlerExecutePool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
