package com.cjyfff.dq.task.transport;

import com.cjyfff.dq.common.DefaultWebApiResult;
import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.task.transport.action.TransportAction;
import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jiashen on 2019/2/11.
 */
@RestController
@Slf4j
public class TestController {

    @Autowired
    private TransportAction transportAction;

    @RequestMapping(path = "/test/send", method={RequestMethod.POST})
    public DefaultWebApiResult testSend(@RequestBody TestInfoReqVo reqVo) {
        try {
            TaskTransportReqPacket reqPacket = new TaskTransportReqPacket();

            reqPacket.setTaskId(reqVo.getTaskId());

            transportAction.sendMsg(reqVo.getNodeId(), reqPacket);

            return DefaultWebApiResult.success();
        } catch (ApiException ae) {
            return DefaultWebApiResult.failure(ae.getCode(), ae.getMsg());
        } catch (Exception e) {
            log.error("testSend get error: ", e);
            return DefaultWebApiResult.failure(ErrorCodeMsg.SYSTEM_ERROR_CODE, ErrorCodeMsg.SYSTEM_ERROR_MSG);
        }

    }
}


@Getter
@Setter
class TestInfoReqVo {
    private Byte nodeId;

    private String taskId;
}
