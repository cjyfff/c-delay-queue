package com.cjyfff.dq.monitor.service.impl;

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
}
