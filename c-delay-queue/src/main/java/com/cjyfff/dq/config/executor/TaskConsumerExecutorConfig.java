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
public class TaskConsumerExecutorConfig {

    @Bean
    public Executor taskConsumerExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(1000);
        taskExecutor.setKeepAliveSeconds(10);
        taskExecutor.setRejectedExecutionHandler(new CallerRunsPolicy());
        taskExecutor.setThreadNamePrefix("TaskConsumer-");

        taskExecutor.initialize();

        TaskExecutor.taskConsumerExecutor = taskExecutor;
        return taskExecutor;
    }
}
