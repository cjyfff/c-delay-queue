package com.cjyfff.dq.config.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;

public class TaskExecutorHolder {
    public static ThreadPoolTaskExecutor asyncAcceptInnerTaskExecutor;

    public static ThreadPoolTaskExecutor queueConsumerExecutor;

    public static ExecutorService taskExecutor;
}
