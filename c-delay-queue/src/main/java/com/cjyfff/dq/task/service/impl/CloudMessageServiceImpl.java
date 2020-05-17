package com.cjyfff.dq.task.service.impl;

import com.alibaba.fastjson.JSON;

import com.cjyfff.dq.common.DefaultWebApiResult;
import com.cjyfff.dq.common.TaskConfig;
import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.common.lock.ZkLock;
import com.cjyfff.dq.common.lock.ZkLockImpl.LockObject;
import com.cjyfff.dq.task.controller.BaseController;
import com.cjyfff.dq.task.service.CloudMessageService;
import com.cjyfff.dq.task.service.PublicMsgService;
import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;
import com.cjyfff.election.config.ZooKeeperClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jiashen on 2020/5/17.
 */
@Slf4j
@Service(version = "1.0.0", protocol = "dubbo")
public class CloudMessageServiceImpl extends BaseController implements CloudMessageService {

    @Autowired
    private PublicMsgService publicMsgService;

    @Autowired
    private ZkLock zkLock;

    @Autowired
    private ZooKeeperClient zooKeeperClient;

    @Override
    public String accessMsg(String acceptMsgDtoStr) {

        AcceptMsgDto reqDto = JSON.parseObject(acceptMsgDtoStr, AcceptMsgDto.class);

        LockObject lockObject = null;
        DefaultWebApiResult result;
        try {
            checkAccessMsgParams(reqDto);

            lockObject = zkLock.idempotentLock(zooKeeperClient.getClient(), TaskConfig.ACCEPT_TASK_LOCK_PATH, reqDto.getNonceStr());
            if (! lockObject.isLockSuccess()) {
                result = DefaultWebApiResult.failure(ErrorCodeMsg.TASK_IS_PROCESSING_CODE, ErrorCodeMsg.TASK_IS_PROCESSING_MSG);
            } else {
                publicMsgService.acceptMsg(reqDto);
                result = DefaultWebApiResult.success();
            }
        } catch (ApiException ae) {
            result =  DefaultWebApiResult.failure(ae.getCode(), ae.getMsg());
        } catch (Exception e) {
            log.error("publicMsgService acceptMsg get error: ", e);
            result = DefaultWebApiResult.failure(ErrorCodeMsg.SYSTEM_ERROR_CODE, ErrorCodeMsg.SYSTEM_ERROR_MSG);
        } finally {
            if (lockObject != null && lockObject.isLockSuccess()) {
                zkLock.tryUnlock(lockObject);
            }
        }

        return JSON.toJSONString(result);
    }
}
