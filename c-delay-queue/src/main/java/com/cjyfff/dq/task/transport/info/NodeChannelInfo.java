package com.cjyfff.dq.task.transport.info;

import java.util.concurrent.ConcurrentSkipListMap;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 19-1-29.
 */
@Getter
@Setter
public class NodeChannelInfo {
    public static ConcurrentSkipListMap<Byte, OneNodeChannelInfo> channelInfoMap;

    @Getter
    @Setter
    public static class OneNodeChannelInfo {
        private Object channel;

        private boolean server;
    }
}
