package com.cjyfff.dq.task.transport.protocol;

import com.cjyfff.dq.task.transport.conf.BaseTransportConf;

/**
 * Created by jiashen on 19-1-29.
 */
public abstract class Packet {
    private Byte version = BaseTransportConf.DEFAULT_PACKET_VERSION;

    private Byte type;

    private Byte nodeId;
}