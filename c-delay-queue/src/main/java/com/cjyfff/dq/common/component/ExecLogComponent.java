package com.cjyfff.dq.common.component;

import java.util.Date;

import com.cjyfff.election.core.info.ShardingInfo;
import com.cjyfff.dq.task.mapper.DelayQueueExecLogMapper;
import com.cjyfff.dq.task.model.DelayQueueExecLog;
import com.cjyfff.dq.task.model.DelayTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 18-10-9.
 */
@Component
public class ExecLogComponent {
    @Autowired
    private DelayQueueExecLogMapper delayQueueExecLogMapper;

    /**
     * 插入 delay task 操作日志，任何对 delay task 的状态修改都要插入一条记录
     * @param delayTask
     * @param taskStatus
     * @param msg
     */
    public void insertLog(DelayTask delayTask, Integer taskStatus, String msg) {
        DelayQueueExecLog delayQueueExecLog = new DelayQueueExecLog();
        delayQueueExecLog.setTaskId(delayTask.getTaskId());
        delayQueueExecLog.setStatus(taskStatus);
        delayQueueExecLog.setSharding(ShardingInfo.getShardingId());
        delayQueueExecLog.setFunctionName(delayTask.getFunctionName());
        delayQueueExecLog.setParams(delayTask.getParams());
        delayQueueExecLog.setMsg(msg);
        delayQueueExecLog.setCreatedAt(new Date());
        delayQueueExecLogMapper.insertSelective(delayQueueExecLog);
    }
}
