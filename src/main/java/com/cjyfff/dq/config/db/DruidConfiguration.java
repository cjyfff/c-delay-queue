package com.cjyfff.dq.config.db;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jiashen on 2018/9/16.
 */
@Configuration
public class DruidConfiguration {
    @Value("${druidOption.allowHost}")
    private String allowHost;

    @Value("${druidOption.denyHost}")
    private String denyHost;

    @Value("${druidOption.loginUsername}")
    private String loginUsername;

    @Value("${druidOption.loginPassword}")
    private String loginPassword;

    @Value("${druidOption.resetEnable}")
    private String resetEnable;

    @Value("${druidOption.addUrlPatterns}")
    private String addUrlPatterns;

    @Value("${druidOption.exclusions}")
    private String exclusions;

    @Bean
    public ServletRegistrationBean registrationBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        //白名单
        bean.addInitParameter("allow", allowHost);
        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
        bean.addInitParameter("deny", denyHost);
        //登录查看信息的账号密码.
        bean.addInitParameter("loginUsername", loginUsername);
        bean.addInitParameter("loginPassword", loginPassword);
        // 禁用HTML页面上的“Reset All”功能.
        bean.addInitParameter("resetEnable", resetEnable);
        return bean;
    }

    @Bean
    public FilterRegistrationBean druidStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean(new WebStatFilter());
        //添加url过滤规则.
        bean.addUrlPatterns(addUrlPatterns);
        //添加不需要忽略的格式信息.
        bean.addInitParameter("exclusions", exclusions);
        return bean;
    }
}
