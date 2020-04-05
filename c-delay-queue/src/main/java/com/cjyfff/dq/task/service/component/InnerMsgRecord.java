package com.cjyfff.dq.task.service.component;

import java.util.concurrent.ConcurrentHashMap;

import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 19-2-15.
 */
@Getter
public class InnerMsgRecord {
    public static ConcurrentHashMap<String, InnerMsgRecordVo> innerMsgRecordMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
    public static class InnerMsgRecordVo {
        public InnerMsgRecordVo(Long sendTime, Integer targetShardingId,
                                TaskTransportReqPacket reqPacket) {
            this.sendTime = sendTime;
            this.targetShardingId = targetShardingId;
            this.reqPacket = reqPacket;
        }

        private Long sendTime;

        private Integer targetShardingId;

        private TaskTransportReqPacket reqPacket;
    }
}
