package com.cjyfff.api.dq;

import com.cjyfff.api.dq.vo.AcceptMsgDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by jiashen on 19-1-18.
 */
@FeignClient("delay-queue-service")
public interface DelayQueueClient {
    @RequestMapping(method = RequestMethod.POST, value = "/dq/acceptMsg")
    Object acceptMsg(@RequestBody AcceptMsgDto reqDto);
}
