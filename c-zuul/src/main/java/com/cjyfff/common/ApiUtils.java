package com.cjyfff.common;

import com.alibaba.fastjson.JSON;

import com.netflix.zuul.context.RequestContext;

/**
 * Created by jiashen on 19-2-19.
 */
public class ApiUtils {
    public static <T> void setJsonResp(RequestContext ctx, T obj) {
        ctx.setResponseBody(JSON.toJSONString(obj));
        ctx.getResponse().setContentType("application/json;charset=UTF-8");
    }
}
