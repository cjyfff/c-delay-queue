package com.cjyfff.dq.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cjyfff
 * @date 2020/5/23 20:39
 */
@Configuration
public class FeignConfigure {
    /**
     * 加上超時重試，重試間隔設爲1s，後端provider下綫不會返回500
     * @return
     */
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(1000L, 5000L, 5);
    }
}
