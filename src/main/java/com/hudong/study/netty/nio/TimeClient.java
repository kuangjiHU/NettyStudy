package com.hudong.study.netty.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/9/4 23:03
 */
public class TimeClient {

    public static void main(String[] args) {
        int port = 8129;
        new  Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
    }
}
