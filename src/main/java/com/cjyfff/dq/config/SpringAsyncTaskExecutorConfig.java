package com.cjyfff.dq.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by jiashen on 2018/10/4.
 */
@Configuration
@ComponentScan("com.cjyfff.dq")
@EnableAsync
public class SpringAsyncTaskExecutorConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(50);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(500);
        taskExecutor.setKeepAliveSeconds(10);
        taskExecutor.setRejectedExecutionHandler(new CallerRunsPolicy());

        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
