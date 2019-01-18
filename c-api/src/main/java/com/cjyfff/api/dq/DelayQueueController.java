package com.cjyfff.api.dq;

import com.cjyfff.api.dq.vo.AcceptMsgDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jiashen on 19-1-18.
 */
@RestController
public class DelayQueueController {

    @Autowired
    private DelayQueueClient delayQueueClient;

    @RequestMapping(value = "/dq/acceptMsg", method = RequestMethod.POST)
    public Object acceptMsg(@RequestBody AcceptMsgDto reqDto) {
        return delayQueueClient.acceptMsg(reqDto);
    }
}
