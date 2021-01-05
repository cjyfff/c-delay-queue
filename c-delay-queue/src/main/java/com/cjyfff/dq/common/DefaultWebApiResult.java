package com.cjyfff.dq.common;

/**
 * Created by jiashen on 2018/9/23.
 */
public class DefaultWebApiResult {
    public final static String SUCCESS_CODE = "0";
    public final static String SUCCESS_MSG = "OK";

    private final static DefaultWebApiResult SUCCESS_WITH_NO_RESULT = new DefaultWebApiResult(SUCCESS_CODE, SUCCESS_MSG);

    private String code;
    private String msg;
    private Object result;

    public DefaultWebApiResult() {
    }

    public DefaultWebApiResult(Object result) {
        this.code = SUCCESS_CODE;
        this.msg = SUCCESS_MSG;
        this.result = result;
    }

    public DefaultWebApiResult(String retCode, String resMsg) {
        this.msg = resMsg;
        this.code = retCode;
    }

    public DefaultWebApiResult(String retCode, String resMsg, Object result) {
        this.code = retCode;
        this.msg = resMsg;
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }


    public static DefaultWebApiResult success() {
        return SUCCESS_WITH_NO_RESULT;
    }

    public static DefaultWebApiResult success(Object data) {
        DefaultWebApiResult result = new DefaultWebApiResult(SUCCESS_MSG, SUCCESS_CODE);
        result.setResult(data);
        return result;
    }

    public static DefaultWebApiResult failure(String errCode, String errMsg) {
        return new DefaultWebApiResult(errMsg, errCode);
    }

    public static DefaultWebApiResult failure(String errCode, String errMsg, Object object) {
        return new DefaultWebApiResult(errCode, errMsg, object);
    }
}
