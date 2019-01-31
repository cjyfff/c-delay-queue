package com.cjyfff.dq.task.queue;

import java.util.Date;

import com.cjyfff.dq.common.TaskHandlerContext;
import com.cjyfff.dq.common.component.AcceptTaskComponent;
import com.cjyfff.dq.common.component.ExecLogComponent;
import com.cjyfff.dq.common.enums.TaskStatus;
import com.cjyfff.dq.task.handler.HandlerResult;
import com.cjyfff.dq.task.handler.ITaskHandler;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.election.core.info.ShardingInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 18-12-29.
 */
@Component
@Slf4j
public class AsyncQueueTaskConsumer {

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private ShardingInfo shardingInfo;

    @Autowired
    private TaskHandlerContext taskHandlerContext;

    @Autowired
    private ExecLogComponent execLogComponent;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Async("taskConsumerExecutor")
    @Transactional(rollbackFor = Exception.class)
    void doConsumer(QueueTask task) {
        // 1、乐观锁更新状态
        // 2、用task id 查出数据
        // 3、处理
        // 4、修改状态
        if (delayTaskMapper.updateStatusByTaskIdAndOldStatus(task.getTaskId(), TaskStatus.IN_QUEUE.getStatus(),
            TaskStatus.PROCESSING.getStatus(), shardingInfo.getNodeId()) > 0) {

            DelayTask delayTask = delayTaskMapper.selectByTaskId(task.getTaskId());

            ITaskHandler taskHandler = taskHandlerContext.getTaskHandler(delayTask.getFunctionName());

            if (taskHandler == null) {
                // 找不到对应方法时，设置任务失败
                String errorMsg = String.format("Can not find handler named %s", delayTask.getFunctionName());
                log.warn(errorMsg);
                Integer taskStatus = TaskStatus.PROCESS_FAIL.getStatus();

                delayTask.setStatus(taskStatus);
                delayTask.setModifiedAt(new Date());
                delayTaskMapper.updateByPrimaryKeySelective(delayTask);

                execLogComponent.insertLog(delayTask, taskStatus, errorMsg);

            } else {
                HandlerResult result = taskHandler.run(delayTask.getParams());

                if (HandlerResult.SUCCESS_CODE.equals(result.getResultCode())) {
                    Integer taskStatus = TaskStatus.PROCESS_SUCCESS.getStatus();

                    delayTask.setStatus(taskStatus);
                    delayTask.setModifiedAt(new Date());
                    delayTaskMapper.updateByPrimaryKeySelective(delayTask);

                    execLogComponent.insertLog(delayTask, taskStatus, "success");

                } else {

                    Integer taskStatus;
                    if (delayTask.getRetryCount() > delayTask.getAlreadyRetryCount()) {
                        // todo: 完善处理重试逻辑
                        task.setExecuteTime(System.currentTimeMillis() * 1000 + delayTask.getDelayTime());
                        acceptTaskComponent.pushToQueue(task);
                        taskStatus = TaskStatus.RETRYING.getStatus();

                    } else if (delayTask.getRetryCount() == 0) {
                        taskStatus = TaskStatus.PROCESS_FAIL.getStatus();

                    } else {
                        taskStatus = TaskStatus.RETRY_FAIL.getStatus();

                    }
                    delayTask.setStatus(taskStatus);
                    delayTask.setModifiedAt(new Date());
                    delayTaskMapper.updateByPrimaryKeySelective(delayTask);

                    execLogComponent.insertLog(delayTask, taskStatus, result.getMsg());
                }
            }

        } else {
            log.warn(String.format("Task: %s can not consume.", task.getTaskId()));
        }
    }
}
