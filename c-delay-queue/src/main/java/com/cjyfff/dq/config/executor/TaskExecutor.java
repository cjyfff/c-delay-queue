package com.cjyfff.dq.config.executor;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class TaskExecutor {

    private ExecutorService taskExecutor;

    public TaskExecutor() {
        // todo: 优化线程池参数
        this.taskExecutor = new ThreadPoolExecutor(50, 70,
                30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), new ThreadPoolExecutor.AbortPolicy());

        TaskExecutorHolder.taskExecutor = this.taskExecutor;
    }

    public ExecutorService getTaskExecutor() {
        return taskExecutor;
    }
}
