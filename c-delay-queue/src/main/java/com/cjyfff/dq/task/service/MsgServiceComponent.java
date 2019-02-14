package com.cjyfff.dq.task.service;

import com.cjyfff.dq.common.enums.TaskStatus;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.vo.dto.BaseMsgDto;

/**
 * Created by jiashen on 19-2-14.
 */
public interface MsgServiceComponent {

    void doPush2Queue(DelayTask delayTask);

    void doPush2Polling(DelayTask delayTask);

    DelayTask createTask(BaseMsgDto reqDto, TaskStatus taskStatus) throws Exception;

    DelayTask createTaskCommit(BaseMsgDto reqDto, TaskStatus taskStatus) throws Exception;
}
