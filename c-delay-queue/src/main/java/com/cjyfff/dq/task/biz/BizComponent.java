package com.cjyfff.dq.task.biz;

import java.util.List;

import com.cjyfff.election.core.info.ShardingInfo;
import com.cjyfff.dq.common.enums.TaskStatus;
import com.cjyfff.dq.common.component.AcceptTaskComponent;
import com.cjyfff.dq.common.component.ExecLogComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.queue.QueueTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 18-12-20.
 */
@Component
public class BizComponent {

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private ExecLogComponent execLogComponent;

    void rePushTaskToQueue() {
        List<DelayTask> myDelayTaskList = delayTaskMapper.selectByStatusAndShardingId(
            TaskStatus.IN_QUEUE.getStatus(), ShardingInfo.getNodeId());

        for (DelayTask delayTask : myDelayTaskList) {
            QueueTask task = new QueueTask(delayTask.getTaskId(), delayTask.getExecuteTime());
            acceptTaskComponent.pushToQueue(task);

            execLogComponent.insertLog(delayTask, TaskStatus.IN_QUEUE.getStatus(),
                String.format("push task in queue when init: %s", delayTask.getTaskId()));
        }
    }
}
