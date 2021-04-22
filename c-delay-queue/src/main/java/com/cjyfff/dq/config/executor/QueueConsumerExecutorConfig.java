package com.cjyfff.dq.config.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by jiashen on 19-1-8.
 */
@Configuration
public class QueueConsumerExecutorConfig {

    @Bean
    public Executor queueConsumerExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(1000);
        taskExecutor.setKeepAliveSeconds(10);
        // todo: 考虑使用其他阻塞策略
        taskExecutor.setRejectedExecutionHandler(new CallerRunsPolicy());
        taskExecutor.setThreadNamePrefix("TaskConsumer-");

        taskExecutor.initialize();

        TaskExecutorHolder.queueConsumerExecutor = taskExecutor;
        return taskExecutor;
    }
}
