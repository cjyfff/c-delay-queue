package com.cjyfff.dq.task.transport.conf;

import lombok.Getter;

/**
 * Created by jiashen on 19-1-29.
 */
@Getter
public class BaseTransportConf {

    public static final Byte DEFAULT_PACKET_VERSION = 1;

    /**
     * 协议识别码
     */
    private static final int P_ID_NUMBER = 0x88888888;
}
