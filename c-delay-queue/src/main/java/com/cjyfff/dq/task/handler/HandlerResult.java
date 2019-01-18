package com.cjyfff.dq.task.handler;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 18-10-8.
 */
@Getter
@Setter
public class HandlerResult {

    public static final Integer SUCCESS_CODE = 0;

    public static final Integer DEFAULT_FAIL_CODE = -1;

    public HandlerResult(Integer resultCode, String msg, String result) {
        this.resultCode = resultCode;
        this.msg = msg;
        this.result = result;
    }

    public HandlerResult(Integer resultCode, String msg) {
        this.resultCode = resultCode;
        this.msg = msg;
    }

    private Integer resultCode;

    private String msg;

    private String result;
}
