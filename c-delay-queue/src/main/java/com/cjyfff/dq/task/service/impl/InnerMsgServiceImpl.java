package com.cjyfff.dq.task.service.impl;

import com.cjyfff.dq.common.enums.TaskStatus;
import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.component.AcceptTaskComponent;
import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.service.InnerMsgService;
import com.cjyfff.dq.task.service.component.MsgServiceComponent;
import com.cjyfff.dq.task.vo.dto.InnerMsgDto;
import com.cjyfff.election.core.info.ShardingInfo;
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

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptMsg(InnerMsgDto reqDto) throws Exception {
        doAcceptMsg(reqDto);
    }

    private void doAcceptMsg(InnerMsgDto reqDto) throws Exception {
        acceptTaskComponent.checkElectionStatus();

        if (! acceptTaskComponent.checkIsMyTask(reqDto.getTaskId())) {
            String errMsg = String.format("%s is not my task", reqDto.getTaskId());
            log.error(errMsg);
            throw new ApiException(ErrorCodeMsg.IS_NOT_MY_TASK_CODE, errMsg);
        }

        // 从主库拿任务信息，不会存在主从未同步的延时问题
        DelayTask delayTask = delayTaskMapper.selectByTaskIdAndStatusForUpdate(TaskStatus.TRANSMITTING.getStatus(), reqDto.getTaskId(),
            ShardingInfo.getShardingId());

        if (delayTask == null) {
            throw new ApiException(ErrorCodeMsg.CAN_NOT_FIND_TASK_ERROR_CODE, ErrorCodeMsg.CAN_NOT_FIND_TASK_ERROR_MSG);
        }

        if (acceptTaskComponent.checkNeedToPushQueueNow(reqDto.getDelayTime())) {
            msgServiceComponent.doPush2Queue(delayTask);
        } else {
            msgServiceComponent.doPush2Polling(delayTask);
        }
    }
}
