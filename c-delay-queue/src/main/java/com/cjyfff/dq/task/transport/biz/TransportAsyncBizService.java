package com.cjyfff.dq.task.transport.biz;

import com.cjyfff.dq.config.TaskConfig;
import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.common.lock.ZkLock;
import com.cjyfff.dq.common.lock.ZkLockImpl.LockObject;
import com.cjyfff.dq.task.service.InnerMsgService;
import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
import com.cjyfff.dq.task.vo.dto.InnerMsgDto;
import com.cjyfff.election.config.ZooKeeperClient;
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

    @Autowired
    private ZkLock zkLock;

    @Autowired
    private ZooKeeperClient zooKeeperClient;

    @Async("acceptInnerTaskExecutor")
    public void asyncAcceptInnerMsg(TaskTransportReqPacket reqPacket) {
        log.info("asyncAcceptInnerMsg begin, task id: {}", reqPacket.getTaskId());
        LockObject lockObject = null;
        try {
            InnerMsgDto innerMsgDto = new InnerMsgDto();
            BeanUtils.copyProperties(reqPacket, innerMsgDto);

            lockObject = zkLock.idempotentLock(zooKeeperClient.getClient(),
                TaskConfig.ACCEPT_TASK_LOCK_PATH, innerMsgDto.getNonceStr());
            if (!lockObject.isLockSuccess()) {
                throw new ApiException(ErrorCodeMsg.TASK_IS_PROCESSING_CODE, ErrorCodeMsg.TASK_IS_PROCESSING_MSG);
            }

            innerMsgService.acceptMsg(innerMsgDto);

        } catch (ApiException ae) {
            log.warn("AsyncAcceptInnerMsg get ApiException: code: {}, msg: {}", ae.getCode(), ae.getMsg());
        } catch (Exception e) {
            log.error("AsyncAcceptInnerMsg get error:", e);
        } finally {
            if (lockObject != null && lockObject.isLockSuccess()){
                zkLock.tryUnlock(lockObject);
            }
        }
    }
}
