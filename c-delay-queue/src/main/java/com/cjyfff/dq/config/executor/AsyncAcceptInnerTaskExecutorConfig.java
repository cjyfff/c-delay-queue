package com.cjyfff.dq.config.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by jiashen on 2019/2/11.
 */
@Configuration
public class AsyncAcceptInnerTaskExecutorConfig {

    @Bean
    public Executor acceptInnerTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(500);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        taskExecutor.setThreadNamePrefix("acceptInnerTask-");

        taskExecutor.initialize();

        TaskExecutorHolder.asyncAcceptInnerTaskExecutor = taskExecutor;
        return taskExecutor;
    }
}
