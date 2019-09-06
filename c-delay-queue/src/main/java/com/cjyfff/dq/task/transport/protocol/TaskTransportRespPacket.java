package com.cjyfff.dq.task.transport.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 19-2-2.
 */
@Getter
@Setter
public class TaskTransportRespPacket extends Packet {
    /**
     * 任务id
     */
    private String taskId;

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 返回结果
     */
    private Object result;
}
