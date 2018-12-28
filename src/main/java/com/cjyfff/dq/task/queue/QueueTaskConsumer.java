package com.cjyfff.dq.task.queue;

import java.util.Date;

import com.cjyfff.election.info.ShardingInfo;
import com.cjyfff.dq.task.common.TaskHandlerContext;
import com.cjyfff.dq.task.common.enums.TaskStatus;
import com.cjyfff.dq.task.common.component.AcceptTaskComponent;
import com.cjyfff.dq.task.common.component.ExecLogComponent;
import com.cjyfff.dq.task.handler.HandlerResult;
import com.cjyfff.dq.task.handler.ITaskHandler;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2018/10/3.
 */
@Component
@Slf4j
public class QueueTaskConsumer {

    @Autowired
    private DelayTaskQueue delayTaskQueue;

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

    /**
     * delay queue consumer
     * 对于这个consumer，实际只需要一个线程，
     * 队列非空时，take方法会阻塞直到队列第一个元素的时间达到，
     * 加入阻塞期间有一个时间更短的元素插入，队列会自动消费更前的那个元素。
     *
     * 这里选择fixedDelay，上一个任务结束后开始计算间隔，并且只有一个线程从队列取数据
     * @throws Exception
     */
    @Scheduled(fixedDelay = 10)
    public void consumer() throws Exception {
        //log.info("---------------");
        // 这里必须用死循环来取元素，
        // 假如通过Scheduled来控制取元素频率的话
        // 两个任务的时间相隔小于Scheduled delay time时，也会变为相隔Scheduled delay time
        while (! delayTaskQueue.queue.isEmpty()) {
            QueueTask task = delayTaskQueue.queue.take();
            log.info(String.format("task %s begin", task.getTaskId()));
            ((QueueTaskConsumer) AopContext.currentProxy()).doConsumer(task);
        }
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void doConsumer(QueueTask task) {
        // 1、乐观锁更新状态
        // 2、用task id 查出数据
        // 3、处理
        // 4、修改状态
        if (delayTaskMapper.updateStatusByOldStatusAndTaskId(TaskStatus.IN_QUEUE.getStatus(),
            TaskStatus.PROCESSING.getStatus(), shardingInfo.getNodeId().byteValue(), task.getTaskId()) > 0) {

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
