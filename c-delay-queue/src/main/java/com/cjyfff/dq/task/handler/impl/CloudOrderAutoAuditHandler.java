package com.cjyfff.dq.task.handler.impl;

import com.alibaba.fastjson.JSON;
import com.cjyfff.dq.common.DefaultWebApiResult;
import com.cjyfff.dq.task.handler.HandlerResult;
import com.cjyfff.dq.task.handler.ITaskHandler;
import com.cjyfff.dq.task.handler.annotation.TaskHandler;
import com.cjyfff.dq.task.handler.impl.ofc.OfcService;
import com.cjyfff.dq.task.handler.vo.OrderAutoAuditHandlerParaVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jiashen on 2020/5/17.
 */
@Slf4j
@Service
@TaskHandler(value = "cloudOrderAutoAuditHandler")
public class CloudOrderAutoAuditHandler implements ITaskHandler {

    @Autowired
    private OfcService ofcService;

    @Override
    public HandlerResult run(String paras) {
        log.info("Run CloudOrderAutoAuditHandler with paras: " + paras);

        OrderAutoAuditHandlerParaVo reqVo = JSON.parseObject(paras, OrderAutoAuditHandlerParaVo.class);

        DefaultWebApiResult result = ofcService.auditOrder(reqVo);
        log.info("result code: {}", result.getCode());
        return new HandlerResult(HandlerResult.SUCCESS_CODE, "success");
    }
}
