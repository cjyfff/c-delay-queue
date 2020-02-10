package com.cjyfff.dq.task.transport.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 19-1-29.
 */
@Getter
@Setter
public abstract class Packet {
    private Byte type;

    private Integer shardingId;
}
