package com.cjyfff.dq.task.controller;

import com.cjyfff.election.config.ZooKeeperClient;
import com.cjyfff.dq.task.common.ApiException;
import com.cjyfff.dq.task.common.BeanValidators;
import com.cjyfff.dq.task.common.DefaultWebApiResult;
import com.cjyfff.dq.task.common.TaskConfig;
import com.cjyfff.dq.task.common.lock.ZkLock;
import com.cjyfff.dq.task.service.InnerMsgService;
import com.cjyfff.dq.task.service.PublicMsgService;
import com.cjyfff.dq.task.service.TestService;
import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;
import com.cjyfff.dq.task.vo.dto.InnerMsgDto;
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
    private InnerMsgService innerMsgService;

    @Autowired
    private TestService testService;

    @Autowired
    private ZkLock zkLock;

    @Autowired
    private ZooKeeperClient zooKeeperClient;

    /**
     * 接收外部消息
     */
    @RequestMapping(path = "/dq/acceptMsg", method={RequestMethod.POST})
    public DefaultWebApiResult acceptMsg(@RequestBody AcceptMsgDto reqDto) {
        try {
            BeanValidators.validateWithParameterException(validator, reqDto);

            if (! zkLock.idempotentLock(zooKeeperClient.getClient(), TaskConfig.ACCEPT_TASK_LOCK_PATH, reqDto.getNonceStr())) {
                return DefaultWebApiResult.failure("-889", "This task is processing...");
            }

            publicMsgService.acceptMsg(reqDto);
            return DefaultWebApiResult.success();
        } catch (ApiException ae) {
            return DefaultWebApiResult.failure(ae.getCode(), ae.getMsg());
        } catch (Exception e) {
            log.error("publicMsgService acceptMsg get error: ", e);
            return DefaultWebApiResult.failure("-1", "system error");
        } finally {
            zkLock.tryUnlock(TaskConfig.ACCEPT_TASK_LOCK_PATH, reqDto.getNonceStr());
        }
    }

    /**
     * 接收内部转发消息
     */
    @RequestMapping(path = "/dq/acceptInnerMsg", method={RequestMethod.POST})
    public DefaultWebApiResult acceptInnerMsg(@RequestBody InnerMsgDto reqDto) {
        try {
            BeanValidators.validateWithParameterException(validator, reqDto);

            if (! zkLock.idempotentLock(zooKeeperClient.getClient(), TaskConfig.ACCEPT_TASK_LOCK_PATH, reqDto.getNonceStr())) {
                return DefaultWebApiResult.failure("-889", "This task is processing...");
            }

            innerMsgService.acceptMsg(reqDto);
            return DefaultWebApiResult.success();
        } catch (ApiException ae) {
            return DefaultWebApiResult.failure(ae.getCode(), ae.getMsg());
        } catch (Exception e) {
            log.error("innerMsgService acceptMsg get error: ", e);
            return DefaultWebApiResult.failure("-1", "system error");
        } finally {
            zkLock.tryUnlock(TaskConfig.ACCEPT_TASK_LOCK_PATH, reqDto.getNonceStr());
        }
    }

    @RequestMapping(path = "/test", method={RequestMethod.POST})
    public DefaultWebApiResult test() {
        testService.test();
        return DefaultWebApiResult.success();
    }
}
