package com.cjyfff.dq.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by jiashen on 2018/10/4.
 */
@Configuration
@EnableScheduling
public class SpringScheduleConfig {
    @Bean
    public Executor taskScheduler() {
        //return new ThreadPoolExecutor(5, 10, 0, TimeUnit.NANOSECONDS, new DelayedWorkQueue());
        return Executors.newScheduledThreadPool(5);
    }
}
