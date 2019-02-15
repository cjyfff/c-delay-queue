package com.cjyfff.dq.monitor.service.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.cjyfff.dq.monitor.controller.vo.MonitorInnerMsgRecordVo;
import com.cjyfff.dq.task.service.component.InnerMsgRecord;
import com.cjyfff.election.core.info.ElectionStatus;
import com.cjyfff.election.core.info.ShardingInfo;
import com.cjyfff.dq.monitor.controller.vo.MonitorNodeInfoVo;
import com.cjyfff.dq.monitor.service.MonitorService;
import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 18-11-23.
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    @Override
    public MonitorNodeInfoVo getNodeInfoVo() {
        MonitorNodeInfoVo infoVo = new MonitorNodeInfoVo();

        infoVo.setShardingMap(ShardingInfo.getShardingMap());
        infoVo.setNodeId(ShardingInfo.getNodeId());
        infoVo.setElectionStatus(ElectionStatus.getElectionStatus().getValue());

        if (ElectionStatus.getLeaderLatch().hasLeadership()) {
            infoVo.setLeader(true);
        } else {
            infoVo.setLeader(false);
        }

        return infoVo;
    }

    @Override
    public MonitorInnerMsgRecordVo getInnerMsgRecord() {

        MonitorInnerMsgRecordVo respVo = new MonitorInnerMsgRecordVo();

        respVo.setRecordAmount(InnerMsgRecord.innerMsgRecordMap.size());

        List<String> taskIds = new ArrayList<>();

        Enumeration<String> taskIdEnums = InnerMsgRecord.innerMsgRecordMap.keys();
        while (taskIdEnums.hasMoreElements()) {
            taskIds.add(taskIdEnums.nextElement());
        }

        respVo.setTaskIds(taskIds);

        return respVo;
    }
}
