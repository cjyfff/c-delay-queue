package com.cjyfff.dq.config.executor;

import com.google.common.collect.ImmutableMap;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

public class DynamicExecutorTypeSelector {

    private static final Map<Integer, ThreadPoolTaskExecutor> EXECUTOR_TYPE_MAP =
            ImmutableMap.<Integer, ThreadPoolTaskExecutor>builder()
                    .put(1, TaskExecutorHolder.asyncAcceptInnerTaskExecutor)
                    .put(2, TaskExecutorHolder.queueConsumerExecutor)
                    .put(3, TaskExecutorHolder.taskExecutor)
                    .build();

    public static ThreadPoolTaskExecutor getExecutorByType(Integer type) {
        return EXECUTOR_TYPE_MAP.get(type);
    }
}
