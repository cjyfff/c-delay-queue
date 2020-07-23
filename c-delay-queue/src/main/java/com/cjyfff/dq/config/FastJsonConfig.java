package com.cjyfff.dq.config;

import com.alibaba.fastjson.parser.ParserConfig;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jiashen on 2020/7/23.
 */
@Configuration
public class FastJsonConfig implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        ParserConfig.getGlobalInstance().setSafeMode(true);
    }
}
