package com.hudong.study.netty.bio;

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
        int port = 8129;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        try (ServerSocket server = new ServerSocket(port)) {
            Socket socket = null;
            while (true) {
                socket = server.accept();
                TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 10000);
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
