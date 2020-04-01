package com.cjyfff.dq.common.error;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 2018/9/23.
 */
@Getter
@Setter
public class ApiException extends Exception {
    private String code;
    private String msg;

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public ApiException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
