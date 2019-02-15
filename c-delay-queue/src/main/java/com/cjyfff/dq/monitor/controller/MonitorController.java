package com.cjyfff.dq.monitor.controller;

import com.cjyfff.dq.monitor.controller.vo.MonitorInnerMsgRecordVo;
import com.cjyfff.dq.monitor.controller.vo.MonitorNodeInfoVo;
import com.cjyfff.dq.monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jiashen on 18-11-23.
 */
@RestController
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @RequestMapping(path = "/monitor/nodeInfo", method={RequestMethod.GET})
    public MonitorNodeInfoVo getNodeInfoVo() {
        return monitorService.getNodeInfoVo();
    }

    @RequestMapping(path = "/monitor/innerMsgRecord", method={RequestMethod.GET})
    public MonitorInnerMsgRecordVo getInnerMsgRecord() {
        return monitorService.getInnerMsgRecord();
    }
}
