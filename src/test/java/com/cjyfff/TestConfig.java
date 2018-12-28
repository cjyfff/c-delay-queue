package com.cjyfff;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by jiashen on 18-11-1.
 */
@ComponentScan(basePackages={"com.cjyfff.dq"})
@EnableTransactionManagement
@Configuration
public class TestConfig {
}
