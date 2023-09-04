package com.hudong.study.netty.bio;

import java.util.concurrent.*;

/**
 * @author Administrator
 * @version 1.0
 * @date 2023/9/4 22:36
 */
public class TimeServerHandlerExecutePool {

    private ExecutorService executor;

    public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
        this.executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                maxPoolSize, 120L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize));
    }


    public void execute(Runnable task) {
        executor.execute(task);
    }


}
