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

        taskExecutor.setCorePoolSize(50);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(500);
        taskExecutor.setKeepAliveSeconds(10);
        taskExecutor.setRejectedExecutionHandler(new CallerRunsPolicy());
        taskExecutor.setThreadNamePrefix("TaskConsumer-");

        taskExecutor.initialize();
        return taskExecutor;
    }
}
