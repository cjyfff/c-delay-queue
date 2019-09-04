package com.cjyfff.dq.task.schedule;

import java.util.Map.Entry;

import com.cjyfff.dq.task.service.component.InnerMsgRecord;
import com.cjyfff.dq.task.service.component.InnerMsgRecord.InnerMsgRecordVo;
import com.cjyfff.dq.task.transport.action.TransportAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 把超过指定时间，没有发送成功的内部任务重新发送
 * Created by jiashen on 19-2-15.
 */
@Slf4j
@Component
public class ReSendInnerMsg {

    private static final int INNER_MSG_TIME_OUT_SECONDS = 30;

    @Autowired
    private TransportAction transportAction;

    @Scheduled(fixedRate = 25 * 1000)
    public void run() {
        log.debug("Start to execute ReSendInnerMsg...");

        for (Entry<String, InnerMsgRecordVo> record : InnerMsgRecord.innerMsgRecordMap.entrySet()) {
            long nowMillis = System.currentTimeMillis();
            if (nowMillis - record.getValue().getSendTime() > INNER_MSG_TIME_OUT_SECONDS * 1000) {
                log.info("Start to re-send msg, task id: {}", record.getKey());
                try {
                    transportAction.sendMsg(record.getValue().getTargetShardingId(), record.getValue().getReqPacket());
                    record.getValue().setSendTime(nowMillis);
                } catch (Exception e) {
                    log.error("transportAction sendMsg method get error: ", e);
                }
            }
        }
    }
}
