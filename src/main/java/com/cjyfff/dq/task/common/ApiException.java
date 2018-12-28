package com.cjyfff.dq.task.common;

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

    public ApiException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
