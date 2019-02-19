package com.cjyfff.filter;

import javax.servlet.http.HttpServletRequest;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 19-2-19.
 */
@Slf4j
@Component
public class RateLimitFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConfig.RATE_LIMIT_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConfig.RATE_LIMIT_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        return request.getRequestURI().contains("dq/acceptMsg");
    }

    @Override
    public Object run() {
        log.info("Begin RateLimitFilter...");

        // 具体逻辑暂不实现了，单机版用guava的RateLimitFilter，分布式版的话用redis的原子加
        return null;
    }
}
