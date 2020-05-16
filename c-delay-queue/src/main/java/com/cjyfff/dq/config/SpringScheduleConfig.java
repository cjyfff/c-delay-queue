package com.cjyfff.dq.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by jiashen on 2018/10/4.
 */
@Configuration
@EnableScheduling
public class SpringScheduleConfig {
    // 新版spring boot 直接通过 spring.task.scheduling.pool.size 设置定时任务线程数
    //@Bean
    //public Executor taskScheduler() {
    //    //return new ThreadPoolExecutor(5, 10, 0, TimeUnit.NANOSECONDS, new DelayedWorkQueue());
    //    return Executors.newScheduledThreadPool(5);
    //}
}
