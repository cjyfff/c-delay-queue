package com.cjyfff.filter;

import javax.servlet.http.HttpServletRequest;

import com.cjyfff.common.ApiUtils;
import com.cjyfff.common.DefaultWebApiResult;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by jiashen on 19-2-19.
 */
@Slf4j
@Component
public class APIAuthFilter extends ZuulFilter {
    @Value("${c-zuul.api-auth-token-name}")
    private String apiAuthTokenName;

    @Value("${c-zuul.api-auth-token-value}")
    private String apiAuthTokenValue;

    @Override
    public String filterType() {
        return FilterConfig.AUTH_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConfig.AUTH_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String accessToken = request.getHeader(apiAuthTokenName);
        if(StringUtils.isEmpty(accessToken) || ! apiAuthTokenValue.equals(accessToken)) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);

            DefaultWebApiResult result = DefaultWebApiResult.failure("-2", "token is invalid.");
            ApiUtils.setJsonResp(ctx, result);
            return null;
        }
        return null;
    }
}
