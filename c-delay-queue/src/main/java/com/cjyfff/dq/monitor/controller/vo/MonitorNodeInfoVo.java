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

    private Map<Byte, String> shardingMap;

    private Byte shardingId;

    private Integer electionStatus;

    private boolean isLeader;
}
