package com.cjyfff.dq.task.transport.biz;

import com.cjyfff.dq.task.service.InnerMsgService;
import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
import com.cjyfff.dq.task.vo.dto.InnerMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 2019/2/11.
 */
@Service
@Slf4j
public class TransportAsyncBizService {

    @Autowired
    private InnerMsgService innerMsgService;

    @Async("acceptInnerTaskExecutor")
    public void asyncAcceptInnerMsg(TaskTransportReqPacket reqPacket) {
        log.info("asyncAcceptInnerMsg begin, task id: {}", reqPacket.getTaskId());
        try {
            InnerMsgDto innerMsgDto = new InnerMsgDto();
            BeanUtils.copyProperties(reqPacket, innerMsgDto);

            innerMsgService.acceptMsg(innerMsgDto);

        } catch (Exception e) {
            log.error("AsyncAcceptInnerMsg get error:", e);
        }
    }
}
