package com.cjyfff.dq.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by jiashen on 2018/12/16.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true, exposeProxy=true)
public class SpringAOPConfig {
}
