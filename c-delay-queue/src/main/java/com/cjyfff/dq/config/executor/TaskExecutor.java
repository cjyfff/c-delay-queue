package com.cjyfff.dq.config.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class TaskExecutor {
    public static ThreadPoolTaskExecutor asyncAcceptInnerTaskExecutor;

    public static ThreadPoolTaskExecutor taskConsumerExecutor;
}
