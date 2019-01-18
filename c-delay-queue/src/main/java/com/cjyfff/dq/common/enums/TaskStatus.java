package com.cjyfff.dq.common.enums;


/**
 * Created by jiashen on 2018/9/24.
 */
public enum TaskStatus {
    ACCEPT(0, "已接收"),
    POLLING(50, "轮询任务处理中"),
    IN_QUEUE(60, "队列中"),
    PROCESSING(100, "执行中"),
    TRANSMITING(101, "转发中"),
    RETRYING(150, "重试中"),
    PROCESS_SUCCESS(200, "执行成功"),
    PROCESS_FAIL(400, "执行失败"),
    RETRY_FAIL(410, "重试失败");

    private Integer status;

    private String desc;

    TaskStatus(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
