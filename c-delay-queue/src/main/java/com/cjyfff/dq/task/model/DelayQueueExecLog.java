package com.cjyfff.dq.task.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DelayQueueExecLog {
    private Long id;

    private String taskId;

    private Integer status;

    private Integer sharding;

    private String functionName;

    private String params;

    private String msg;

    private Long taskResultId;

    private Date createdAt;
}