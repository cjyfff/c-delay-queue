package com.cjyfff.dq.task.transport.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 19-2-2.
 */
@Getter
@Setter
public class TaskTransportRespPacket extends Packet {
    private String code;

    private String msg;

    private Object result;
}
