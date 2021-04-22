package com.cjyfff.dq.config.executor;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TaskExecutor {

    private ExecutorService taskExecutor;

    public TaskExecutor() {
        // todo: 优化线程池参数
        this.taskExecutor = Executors.newSingleThreadExecutor();

        TaskExecutorHolder.taskExecutor = this.taskExecutor;
    }

    public ExecutorService getTaskExecutor() {
        return taskExecutor;
    }
}
