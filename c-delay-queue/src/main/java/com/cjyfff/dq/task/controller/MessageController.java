package com.cjyfff.dq.task.controller;

import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.common.lock.ZkLockImpl.LockObject;
import com.cjyfff.dq.task.vo.dto.BaseMsgDto;
import com.cjyfff.election.config.ZooKeeperClient;
import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.BeanValidators;
import com.cjyfff.dq.common.DefaultWebApiResult;
import com.cjyfff.dq.common.TaskConfig;
import com.cjyfff.dq.common.lock.ZkLock;
import com.cjyfff.dq.task.service.PublicMsgService;
import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jiashen on 18-8-17.
 */
@RestController
@Slf4j
public class MessageController extends BaseController {

    @Autowired
    private PublicMsgService publicMsgService;

    @Autowired
    private ZkLock zkLock;

    @Autowired
    private ZooKeeperClient zooKeeperClient;

    /**
     * 接收外部消息
     */
    @RequestMapping(path = "/dq/acceptMsg", method={RequestMethod.POST})
    public DefaultWebApiResult acceptMsg(@RequestBody AcceptMsgDto reqDto) {

        LockObject lockObject = null;
        try {
            checkParams(reqDto);

            lockObject = zkLock.idempotentLock(zooKeeperClient.getClient(), TaskConfig.ACCEPT_TASK_LOCK_PATH, reqDto.getNonceStr());
            if (! lockObject.isLockSuccess()) {
                return DefaultWebApiResult.failure(ErrorCodeMsg.TASK_IS_PROCESSING_CODE, ErrorCodeMsg.TASK_IS_PROCESSING_MSG);
            }

            publicMsgService.acceptMsg(reqDto);
            return DefaultWebApiResult.success();
        } catch (ApiException ae) {
            return DefaultWebApiResult.failure(ae.getCode(), ae.getMsg());
        } catch (Exception e) {
            log.error("publicMsgService acceptMsg get error: ", e);
            return DefaultWebApiResult.failure(ErrorCodeMsg.SYSTEM_ERROR_CODE, ErrorCodeMsg.SYSTEM_ERROR_MSG);
        } finally {
            if (lockObject != null && lockObject.isLockSuccess()) {
                zkLock.tryUnlock(lockObject);
            }
        }
    }

    private void checkParams(BaseMsgDto reqDto) throws ApiException {
        BeanValidators.validateWithParameterException(validator, reqDto);

        if (reqDto.getRetryCount() == null) {
            reqDto.setRetryCount(Byte.valueOf("0"));
        }

        if (reqDto.getRetryInterval() == null) {
            reqDto.setRetryInterval(1);
        }
    }
}
