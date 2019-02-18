package com.cjyfff.dq.monitor.controller.vo;


import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 19-2-15.
 */
@Getter
@Setter
public class MonitorInnerMsgRecordVo {
    private Integer recordAmount;

    private List<String> taskIds;
}
