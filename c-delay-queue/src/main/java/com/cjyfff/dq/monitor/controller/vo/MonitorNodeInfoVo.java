package com.cjyfff.dq.monitor.controller.vo;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 18-11-23.
 */
@Getter
@Setter
public class MonitorNodeInfoVo {

    private Map<Integer, String> shardingMap;

    private Integer shardingId;

    private Integer electionStatus;

    private boolean isLeader;
}
