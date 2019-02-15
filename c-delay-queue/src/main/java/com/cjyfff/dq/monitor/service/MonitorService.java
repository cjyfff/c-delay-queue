package com.cjyfff.dq.monitor.service;

import com.cjyfff.dq.monitor.controller.vo.MonitorInnerMsgRecordVo;
import com.cjyfff.dq.monitor.controller.vo.MonitorNodeInfoVo;

/**
 * Created by jiashen on 18-11-23.
 */
public interface MonitorService {
    MonitorNodeInfoVo getNodeInfoVo();

    MonitorInnerMsgRecordVo getInnerMsgRecord();
}
