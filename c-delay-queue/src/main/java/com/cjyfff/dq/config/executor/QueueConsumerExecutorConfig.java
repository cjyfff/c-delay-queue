package com.cjyfff.dq.config.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by jiashen on 19-1-8.
 */
@Configuration
public class QueueConsumerExecutorConfig {

    /**
     * 此线程池负责读取 DB 中的任务，派发到 TaskExecutor 线程池，本身耗时不大，
     * 因此线程数量不需要设置的太大
     * @return
     */
    @Bean
    public Executor queueConsumerExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(1000);
        taskExecutor.setKeepAliveSeconds(60);
        // todo: 后续，当线程池满了的时候
        //  1、考虑维护一个系统状态，当线程池满时，新增任务返回特殊错误码给调用方，让调用方不再请求。
        //  2、任务更新为失败状态
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        taskExecutor.setThreadNamePrefix("QueueConsumer-");

        taskExecutor.initialize();

        TaskExecutorHolder.queueConsumerExecutor = taskExecutor;
        return taskExecutor;
    }
}
