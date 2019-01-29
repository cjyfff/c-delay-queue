package com.cjyfff.dq.task.transport.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 19-1-29.
 */
@Getter
@Setter
public class TaskTransportPacket extends Packet {
    /**
     * taskId，需要保证唯一
     */
    private String taskId;

    /**
     * 调用方法名
     */
    private String functionName;

    /**
     * 延时时间，单位秒
     */
    private Long delayTime;

    /**
     * 随机字符串，用于保证接口幂等
     */
    private String nonceStr;

    /**
     * 调用参数字符串
     */
    private String params;

    /**
     * 重试次数
     */
    private Byte retryCount;

    /**
     * 重试周期，单位秒
     */
    private Integer retryInterval;
}
