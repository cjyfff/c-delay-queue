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
    public static ConcurrentSkipListMap<Integer, OneNodeChannelInfo> channelInfoMap = new ConcurrentSkipListMap<>();

    @Getter
    @Setter
    public static class OneNodeChannelInfo {
        public OneNodeChannelInfo(Channel channel, boolean con2server) {
            this.channel = channel;
            this.con2server = con2server;
        }

        private Channel channel;

        /**
        标记是否连接到server端
         */
        private boolean con2server;
    }
}
