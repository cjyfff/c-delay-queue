package com.cjyfff.dq.task.transport.biz;

import java.util.concurrent.TimeUnit;

import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 2019/2/11.
 */
@Service
@Slf4j
public class TransportAsyncBizService {

    @Async("acceptInnerTaskExecutor")
    public void asyncAcceptInnerMsg(TaskTransportReqPacket reqPacket) {
        log.info("asyncAcceptInnerMsg begin, task id: {}", reqPacket.getTaskId());
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        log.info("asyncAcceptInnerMsg end, task id: {}", reqPacket.getTaskId());
    }
}
