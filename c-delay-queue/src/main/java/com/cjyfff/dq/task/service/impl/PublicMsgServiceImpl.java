package com.cjyfff.dq.task.service.impl;

import java.util.UUID;

import com.alibaba.fastjson.JSON;

import com.cjyfff.dq.common.enums.TaskStatus;
import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.task.service.MsgServiceComponent;
import com.cjyfff.dq.task.transport.action.TransportAction;
import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
import com.cjyfff.election.core.info.ShardingInfo;
import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.DefaultWebApiResult;
import com.cjyfff.dq.common.HttpUtils;
import com.cjyfff.dq.common.TaskHandlerContext;
import com.cjyfff.dq.task.handler.ITaskHandler;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.service.PublicMsgService;
import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;
import com.cjyfff.dq.common.component.AcceptTaskComponent;
import com.cjyfff.dq.task.vo.dto.InnerMsgDto;
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

    @Value("${delay_queue.task_rate_limit_permits}")
    private double taskRateLimitPermits;

    @Value("${delay_queue.enable_task_rate_limit}")
    private boolean enableTaskRateLimit;

    private RateLimiter rateLimiter;

    public PublicMsgServiceImpl(@Value("${delay_queue.enable_task_rate_limit}") boolean etl,
                                @Value("${delay_queue.task_rate_limit_permits}") double tlp) {
        if (etl) {
            if (tlp > 0) {
                rateLimiter = RateLimiter.create(tlp);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptMsg(AcceptMsgDto reqDto) throws Exception {
        // 限流逻辑
        if (enableTaskRateLimit) {
            if (rateLimiter == null) {
                throw new ApiException(ErrorCodeMsg.SYSTEM_ERROR_CODE, "RateLimiter is not initialization, check the configuration.");
            }
            if (! rateLimiter.tryAcquire(1)) {
                log.warn("Reach task rate limit, request params is: " + JSON.toJSON(reqDto));
                return;
            }
        }

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

            msgServiceComponent.createTaskCommit(reqDto, TaskStatus.TRANSMITING);

            Byte targetShardingId = acceptTaskComponent.getShardingIdByTaskId(reqDto.getTaskId());
            //String targetHost = ShardingInfo.getShardingMap().get(targetShardingId);
            //
            //if (targetHost == null) {
            //    throw new ApiException(ErrorCodeMsg.CAN_NOT_GET_SHARDING_INFO_CODE,
            //        String.format("Can not get sharding info, sharding id: %s, task id: %s",
            //            targetShardingId.toString(), reqDto.getTaskId()));
            //}
            //
            //String url = String.format("http://%s/dq/acceptInnerMsg", targetHost);
            //String nonceStr = UUID.randomUUID().toString().replace("-", "");
            //InnerMsgDto innerMsgDto = new InnerMsgDto();
            //
            //BeanUtils.copyProperties(reqDto, innerMsgDto);
            //innerMsgDto.setNonceStr(nonceStr);
            //try {
            //    sendInnerTaskMsg(url, innerMsgDto, targetShardingId, targetHost);
            //} catch (Exception err) {
            //    log.error("Send inner task get error: ", err);
            //    throw err;
            //}

            TaskTransportReqPacket reqPacket = new TaskTransportReqPacket();
            BeanUtils.copyProperties(reqDto, reqPacket);
            String nonceStr = UUID.randomUUID().toString().replace("-", "");
            reqPacket.setNonceStr(nonceStr);

            // todo: 添加上发送成功校验
            transportAction.sendMsg(targetShardingId, reqPacket);
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

    private void sendInnerTaskMsg(String url, InnerMsgDto innerMsgDto,
                                 Byte targetShardingId, String targetHost) throws Exception {
        String resultJson = HttpUtils.doPost(url, JSON.toJSONString(innerMsgDto));

        log.info(String.format("Send inner task msg to node id :%s, host: %s, resp is %s",
            targetShardingId, targetHost, resultJson));

        DefaultWebApiResult result = JSON.parseObject(resultJson, DefaultWebApiResult.class);
        if (!DefaultWebApiResult.SUCCESS_CODE.equals(result.getCode())) {
            log.error("Send inner task get error: " + result.getMsg());
            throw new ApiException(ErrorCodeMsg.SEND_INNER_TASK_GET_ERROR_CODE, "Send inner task get error: " + result.getMsg());
        }
    }
}
