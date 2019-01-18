package com.cjyfff.dq.task.service.impl;

import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.component.AcceptTaskComponent;
import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.service.InnerMsgService;
import com.cjyfff.dq.task.vo.dto.InnerMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 18-10-9.
 */
@Slf4j
@Service
public class InnerMsgServiceImpl implements InnerMsgService {

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private MsgServiceComponent msgServiceComponent;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptMsg(InnerMsgDto reqDto) throws Exception {
        acceptTaskComponent.checkElectionStatus();

        if (! acceptTaskComponent.checkIsMyTask(reqDto.getTaskId())) {
            String errMsg = String.format("%s is not my task", reqDto.getTaskId());
            log.error(errMsg);
            throw new ApiException(ErrorCodeMsg.IS_NOT_MY_TASK_CODE, errMsg);
        }

        DelayTask delayTask = msgServiceComponent.createTask(reqDto);

        if (acceptTaskComponent.checkNeedToPushQueueNow(reqDto.getDelayTime())) {
            msgServiceComponent.doPush2Queue(delayTask);
        } else {
            msgServiceComponent.doPush2Polling(delayTask);
        }
    }
}
