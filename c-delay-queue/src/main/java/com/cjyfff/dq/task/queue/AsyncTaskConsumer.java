package com.cjyfff.dq.task.queue;

import java.util.Date;
import java.util.concurrent.*;

import com.alibaba.fastjson.JSON;
import com.cjyfff.dq.common.TaskHandlerContext;
import com.cjyfff.dq.config.executor.TaskExecutor;
import com.cjyfff.dq.task.component.AcceptTaskComponent;
import com.cjyfff.dq.task.component.ExecLogComponent;
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

import static com.cjyfff.dq.task.handler.HandlerResult.*;

/**
 * Created by jiashen on 18-12-29.
 */
@Component
@Slf4j
public class AsyncTaskConsumer {

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private TaskHandlerContext taskHandlerContext;

    @Autowired
    private ExecLogComponent execLogComponent;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private TaskExecutor taskExecutor;

    @Async("queueConsumerExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void doConsumer(QueueInternalTask task) {
        // 1、乐观锁更新状态
        // 2、用task id 查出数据
        // 3、处理
        // 4、修改状态
        if (delayTaskMapper.updateStatusByTaskIdAndOldStatus(task.getTaskId(), TaskStatus.IN_QUEUE.getStatus(),
            TaskStatus.PROCESSING.getStatus(), ShardingInfo.getShardingId()) > 0) {

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

                FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        HandlerResult result = taskHandler.run(delayTask.getParams());
                        return JSON.toJSONString(result);
                    }
                });
                taskExecutor.getTaskExecutor().submit(futureTask);

                String resultStr;
                try {
                    if (delayTask.getExecuteTimeout() == null || delayTask.getExecuteTimeout() <= 0) {
                        resultStr = futureTask.get();
                    } else {
                        resultStr = futureTask.get(delayTask.getExecuteTimeout(), TimeUnit.SECONDS);
                    }
                } catch (TimeoutException e) {
                    log.info("job time out, id: {}", delayTask.getTaskId());
                    HandlerResult timeoutResult = new HandlerResult(DEFAULT_TIMEOUT_CODE, DEFAULT_TIMEOUT_MSG);
                    resultStr = JSON.toJSONString(timeoutResult);
                } catch (Exception e) {
                    log.error("doConsumer get error: ", e);
                    HandlerResult timeoutResult = new HandlerResult(DEFAULT_SYSTEM_ERROR_CODE, DEFAULT_SYSTEM_ERROR_MSG);
                    resultStr = JSON.toJSONString(timeoutResult);
                }

                HandlerResult result = JSON.parseObject(resultStr, HandlerResult.class);

                if (HandlerResult.SUCCESS_CODE.equals(result.getResultCode())) {
                    Integer taskStatus = TaskStatus.PROCESS_SUCCESS.getStatus();

                    delayTask.setStatus(taskStatus);
                    delayTask.setModifiedAt(new Date());
                    delayTaskMapper.updateByPrimaryKeySelective(delayTask);

                    execLogComponent.insertLog(delayTask, taskStatus, "success");

                } else {

                    Integer taskStatus;
                    if (delayTask.getRetryCount() > delayTask.getAlreadyRetryCount()) {
                        task.setExecuteTime(System.currentTimeMillis() * 1000 + delayTask.getDelayTime());
                        acceptTaskComponent.pushToQueue(task);
                        taskStatus = TaskStatus.RETRYING.getStatus();

                    } else {
                        if (HandlerResult.DEFAULT_TIMEOUT_CODE.equals(result.getResultCode())) {
                            taskStatus = TaskStatus.TIMEOUT.getStatus();
                        } else {
                            taskStatus = TaskStatus.PROCESS_FAIL.getStatus();
                        }

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
