package com.cjyfff.dq.task.biz;

import java.util.Date;
import java.util.List;

import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.election.core.info.ShardingInfo;
import com.cjyfff.dq.common.enums.TaskStatus;
import com.cjyfff.dq.common.component.AcceptTaskComponent;
import com.cjyfff.dq.common.component.ExecLogComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.queue.QueueInternalTask;
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
    private ExecLogComponent execLogComponent;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    void reHandleTask() {

        reHandleTransmittingTask();

        rePushTaskToQueue();
    }


    private void rePushTaskToQueue() {
        List<DelayTask> myDelayTaskList = delayTaskMapper.selectByStatusAndShardingId(
            TaskStatus.IN_QUEUE.getStatus(), ShardingInfo.getShardingId());

        for (DelayTask delayTask : myDelayTaskList) {
            QueueInternalTask task = new QueueInternalTask(delayTask.getTaskId(), delayTask.getExecuteTime());
            acceptTaskComponent.pushToQueue(task);

            execLogComponent.insertLog(delayTask, TaskStatus.IN_QUEUE.getStatus(),
                String.format("push task in queue when init: %s", delayTask.getTaskId()));
        }
    }

    private void reHandleTransmittingTask() {
        List<DelayTask> transmittingDelayTaskList = delayTaskMapper.selectByStatusAndShardingId(
            TaskStatus.TRANSMITTING.getStatus(), ShardingInfo.getShardingId());

        for (DelayTask delayTask : transmittingDelayTaskList) {
            if (acceptTaskComponent.checkNeedToPushQueueNow(delayTask.getDelayTime())) {
                delayTask.setStatus(TaskStatus.IN_QUEUE.getStatus());
            } else {
                delayTask.setStatus(TaskStatus.POLLING.getStatus());
            }

            delayTask.setModifiedAt(new Date());
            delayTaskMapper.updateByPrimaryKeySelective(delayTask);
        }
    }
}
