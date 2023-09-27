package com.hudong.study.netty.aio;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/9/27 22:42
 */
public class AioTimeServer {
    public static void main(String[] args) throws InterruptedException {
        int port = 8129;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                //
            }
        }
        new Thread(new AsyncTimeServerHandler(port)).start();
        Thread.currentThread().join();
    }

}
