package com.cjyfff.dq.config.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class TaskExecutor {

    private final ThreadPoolTaskExecutor taskExecutor;

    public TaskExecutor() {
        this.taskExecutor = new ThreadPoolTaskExecutor();

        this.taskExecutor.setCorePoolSize(50);
        this.taskExecutor.setMaxPoolSize(70);
        this.taskExecutor.setQueueCapacity(1000);
        this.taskExecutor.setKeepAliveSeconds(60);
        // todo: 后续，当线程池满了的时候
        //  1、考虑维护一个系统状态，当线程池满时，新增任务返回特殊错误码给调用方，让调用方不再请求。
        //  2、任务更新为失败状态
        this.taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        this.taskExecutor.setThreadNamePrefix("TaskExecutor-");

        this.taskExecutor.initialize();

        TaskExecutorHolder.taskExecutor = this.taskExecutor;
    }

    public ThreadPoolTaskExecutor getTaskExecutor() {
        return taskExecutor;
    }
}
