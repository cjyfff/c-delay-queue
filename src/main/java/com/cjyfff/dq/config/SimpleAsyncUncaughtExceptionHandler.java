package com.cjyfff.dq.config;

import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

/**
 * Created by jiashen on 18-12-4.
 */
@Slf4j
public class SimpleAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error(String.format("Unexpected error occurred invoking async " +
            "method '%s'.", method), ex);
    }
}
