package com.cjyfff.dq.monitor.controller;

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
    private MonitorService monitorServicel;

    @RequestMapping(path = "/monitor/nodeInfo", method={RequestMethod.GET})
    public MonitorNodeInfoVo getNodeInfoVo() {
        return monitorServicel.getNodeInfoVo();
    }
}
