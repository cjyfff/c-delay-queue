package com.cjyfff.dq.task.service.component;

import java.util.Date;

import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.enums.TaskStatus;
import com.cjyfff.dq.task.component.AcceptTaskComponent;
import com.cjyfff.dq.task.component.ExecLogComponent;
import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.queue.QueueInternalTask;
import com.cjyfff.dq.task.vo.dto.BaseMsgDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 18-12-20.
 */
@Component
public class MsgServiceComponent {

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private ExecLogComponent execLogComponent;

    public void doPush2Queue(DelayTask delayTask) {
        QueueInternalTask task = new QueueInternalTask(delayTask.getTaskId(), delayTask.getExecuteTime());
        acceptTaskComponent.pushToQueue(task);
        delayTask.setStatus(TaskStatus.IN_QUEUE.getStatus());
        delayTask.setModifiedAt(new Date());
        delayTaskMapper.updateByPrimaryKeySelective(delayTask);

        execLogComponent.insertLog(delayTask, TaskStatus.IN_QUEUE.getStatus(),
            String.format("In Queue: %s", delayTask.getTaskId()));
    }

    public void doPush2Polling(DelayTask delayTask) {
        delayTask.setStatus(TaskStatus.POLLING.getStatus());
        delayTask.setModifiedAt(new Date());
        delayTaskMapper.updateByPrimaryKeySelective(delayTask);
    }

    /**
     * 任务持久化
     * 为了简化逻辑，createTask不新建事务，出现异常都回滚，由调用方重试
     * 因为程序总会有概率的出现异常的，假如出现异常时，任务已经创建，并且通知到调用方的话，
     * 调用方会重复发送请求，这样的话任务就会重复被创建
     * @param reqDto
     */
    public DelayTask createTask(BaseMsgDto reqDto, TaskStatus taskStatus) throws Exception {
        DelayTask oldDelayTask = delayTaskMapper.selectByTaskIdForUpdate(reqDto.getTaskId());
        if (oldDelayTask != null) {
            throw new ApiException(ErrorCodeMsg.TASK_ID_EXIST_CODE, ErrorCodeMsg.TASK_ID_EXIST_MSG);
        }

        DelayTask delayTask = new DelayTask();

        delayTask.setTaskId(reqDto.getTaskId());
        delayTask.setFunctionName(reqDto.getFunctionName());
        delayTask.setParams(reqDto.getParams());
        delayTask.setRetryCount(reqDto.getRetryCount());
        delayTask.setRetryInterval(reqDto.getRetryInterval());
        delayTask.setDelayTime(reqDto.getDelayTime());
        delayTask.setExecuteTime(System.currentTimeMillis() / 1000 + reqDto.getDelayTime());
        delayTask.setStatus(taskStatus.getStatus());
        delayTask.setCreatedAt(new Date());
        delayTask.setModifiedAt(new Date());
        delayTask.setShardingId(acceptTaskComponent.getShardingIdByTaskId(reqDto.getTaskId()));

        delayTaskMapper.insert(delayTask);

        execLogComponent.insertLog(delayTask, TaskStatus.ACCEPT.getStatus(), String.format("Insert task: %s", reqDto.getTaskId()));

        return delayTask;
    }

    /**
     * 开启一个新的事务，进行任务创建，调用方即时出现异常，创建的数据也不回滚
     * @param reqDto
     * @param taskStatus
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public DelayTask createTaskCommit(BaseMsgDto reqDto, TaskStatus taskStatus) throws Exception {
        return createTask(reqDto, taskStatus);
    }
}
