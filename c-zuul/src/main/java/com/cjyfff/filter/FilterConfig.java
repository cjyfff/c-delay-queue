package com.cjyfff.filter;

/**
 * Created by jiashen on 19-2-19.
 */
public interface FilterConfig {
    String AUTH_FILTER_TYPE = "pre";

    int AUTH_FILTER_ORDER = 0;

    String RATE_LIMIT_FILTER_TYPE = "pre";

    int RATE_LIMIT_FILTER_ORDER = 1;
}
