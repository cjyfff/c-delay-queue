package com.cjyfff.dq.task.transport.info;

import java.util.concurrent.ConcurrentSkipListMap;

import io.netty.channel.Channel;
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
        public OneNodeChannelInfo(Channel channel, boolean server) {
            this.channel = channel;
            this.server = server;
        }

        private Channel channel;

        private boolean server;
    }
}
