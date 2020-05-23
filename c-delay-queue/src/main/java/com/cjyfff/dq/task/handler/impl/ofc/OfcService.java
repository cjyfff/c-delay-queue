package com.cjyfff.dq.task.handler.impl.ofc;

import com.cjyfff.dq.common.DefaultWebApiResult;
import com.cjyfff.dq.task.handler.vo.OrderAutoAuditHandlerParaVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author cjyfff
 * @date 2020/5/23 18:06
 */
@FeignClient(value = "ofc-server")
public interface OfcService {
    @RequestMapping(method = RequestMethod.POST, value = "/auditOrder")
    DefaultWebApiResult auditOrder(@RequestBody OrderAutoAuditHandlerParaVo reqVo);
}
