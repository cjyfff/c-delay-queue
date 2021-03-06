package com.cjyfff.dq.task.service.impl;

import java.util.UUID;

import com.alibaba.fastjson.JSON;

import com.cjyfff.dq.common.enums.TaskStatus;
import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.task.service.component.InnerMsgRecord;
import com.cjyfff.dq.task.service.component.InnerMsgRecord.InnerMsgRecordVo;
import com.cjyfff.dq.task.service.component.MsgServiceComponent;
import com.cjyfff.dq.task.transport.action.TransportAction;
import com.cjyfff.dq.task.transport.protocol.PacketType;
import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
import com.cjyfff.election.core.info.ShardingInfo;
import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.TaskHandlerContext;
import com.cjyfff.dq.task.handler.ITaskHandler;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.service.PublicMsgService;
import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;
import com.cjyfff.dq.task.component.AcceptTaskComponent;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2018/9/23.
 */
@Slf4j
@Service
public class PublicMsgServiceImpl implements PublicMsgService {

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private TaskHandlerContext taskHandlerContext;

    @Autowired
    private MsgServiceComponent msgServiceComponent;

    @Autowired
    private TransportAction transportAction;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptMsg(AcceptMsgDto reqDto) throws Exception {

        acceptTaskComponent.checkElectionStatus();

        checkFunctionName(reqDto.getFunctionName());

        if (acceptTaskComponent.checkIsMyTask(reqDto.getTaskId())) {
            DelayTask newDelayTask = msgServiceComponent.createTask(reqDto, TaskStatus.ACCEPT);
            if (acceptTaskComponent.checkNeedToPushQueueNow(newDelayTask.getDelayTime())) {
                msgServiceComponent.doPush2Queue(newDelayTask);
            } else {
                msgServiceComponent.doPush2Polling(newDelayTask);
            }
        } else {
            // 转发到对应机器

            msgServiceComponent.createTaskCommit(reqDto, TaskStatus.TRANSMITTING);

            sendInnerTaskMsg(reqDto);
        }

    }

    private void checkFunctionName(String functionName) throws ApiException {
        ITaskHandler taskHandler = taskHandlerContext.getTaskHandler(functionName);
        if (taskHandler == null) {
            throw new ApiException(
                ErrorCodeMsg.CAN_NOT_FIND_HANDLER_BY_SPECIFIED_FUNCTION_NAME_CODE,
                ErrorCodeMsg.CAN_NOT_FIND_HANDLER_BY_SPECIFIED_FUNCTION_NAME_MSG);
        }
    }

    private void sendInnerTaskMsg(AcceptMsgDto reqDto) throws Exception {
        Integer targetShardingId = acceptTaskComponent.getShardingIdByTaskId(reqDto.getTaskId());

        TaskTransportReqPacket reqPacket = new TaskTransportReqPacket();
        BeanUtils.copyProperties(reqDto, reqPacket);
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        reqPacket.setNonceStr(nonceStr);
        reqPacket.setShardingId(ShardingInfo.getShardingId());
        reqPacket.setType(PacketType.TASK_TRANSPORT_REQ);

        // 记录发送信息
        InnerMsgRecord.innerMsgRecordMap.put(reqDto.getTaskId(),
            new InnerMsgRecordVo(System.currentTimeMillis(), targetShardingId, reqPacket));

        transportAction.sendMsg(targetShardingId, reqPacket);
    }
}
