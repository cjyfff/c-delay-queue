package com.cjyfff.dq.config.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


public class TaskExecutorHolder {
    public static ThreadPoolTaskExecutor asyncAcceptInnerTaskExecutor;

    public static ThreadPoolTaskExecutor queueConsumerExecutor;

    public static ThreadPoolTaskExecutor taskExecutor;
}
