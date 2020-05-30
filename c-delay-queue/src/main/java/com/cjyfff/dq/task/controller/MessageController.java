package com.cjyfff.dq.task.controller;

import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.common.lock.ZkLockImpl.LockObject;
import com.cjyfff.dq.task.vo.dto.BaseMsgDto;
import com.cjyfff.election.config.ZooKeeperClient;
import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.BeanValidators;
import com.cjyfff.dq.common.DefaultWebApiResult;
import com.cjyfff.dq.config.TaskConfig;
import com.cjyfff.dq.common.lock.ZkLock;
import com.cjyfff.dq.task.service.PublicMsgService;
import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<DefaultWebApiResult> acceptMsg(@RequestBody AcceptMsgDto reqDto) {

        LockObject lockObject = null;
        try {
            checkAccessMsgParams(reqDto);

            lockObject = zkLock.idempotentLock(zooKeeperClient.getClient(), TaskConfig.ACCEPT_TASK_LOCK_PATH, reqDto.getNonceStr());
            if (! lockObject.isLockSuccess()) {
                DefaultWebApiResult result = DefaultWebApiResult.failure(ErrorCodeMsg.TASK_IS_PROCESSING_CODE, ErrorCodeMsg.TASK_IS_PROCESSING_MSG);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }

            publicMsgService.acceptMsg(reqDto);
            DefaultWebApiResult result = DefaultWebApiResult.success();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ApiException ae) {
            DefaultWebApiResult result =  DefaultWebApiResult.failure(ae.getCode(), ae.getMsg());
            // 选举未完成时，返回 http 状态为 400，让 nginx 可以切换到其他节点
            if (ErrorCodeMsg.ELECTION_NOT_FINISHED_CODE.equals(ae.getCode())) {
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("publicMsgService acceptMsg get error: ", e);
            DefaultWebApiResult result = DefaultWebApiResult.failure(ErrorCodeMsg.SYSTEM_ERROR_CODE, ErrorCodeMsg.SYSTEM_ERROR_MSG);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } finally {
            if (lockObject != null && lockObject.isLockSuccess()) {
                zkLock.tryUnlock(lockObject);
            }
        }
    }


}
