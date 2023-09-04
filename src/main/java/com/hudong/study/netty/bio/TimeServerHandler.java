package com.hudong.study.netty.bio;


import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/8/31 0:06
 */
public class TimeServerHandler implements Runnable {

    private final Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
             PrintStream out = new PrintStream(this.socket.getOutputStream(), true)) {

            String currentTime;
            String body;
            while (true) {
                body = in.readLine();
                if (body == null) {
                    break;
                }
                System.out.println("The time Server receive order : " + body);
                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                out.println(currentTime);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
