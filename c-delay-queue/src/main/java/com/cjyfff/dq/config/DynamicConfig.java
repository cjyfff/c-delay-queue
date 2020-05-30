package com.cjyfff.dq.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jiashen on 2020/5/30.
 */
@Getter
@Configuration
@RefreshScope
public class DynamicConfig {


    @Value("${delay_queue.enable_task_rate_limit}")
    private boolean enableTaskRateLimit;

    @Value("${delay_queue.task_rate_limit_permits}")
    private double taskRateLimitPermits;

    @Value("${delay_queue.critical_polling_time}")
    private Long criticalPollingTime;

    @Value("${delay_queue.test}")
    private String test;
}
