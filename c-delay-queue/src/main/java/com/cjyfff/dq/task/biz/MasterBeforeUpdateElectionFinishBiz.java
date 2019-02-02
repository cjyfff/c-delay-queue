package com.cjyfff.dq.task.biz;

import java.util.List;

import com.cjyfff.dq.task.transport.action.TransportAction;
import com.cjyfff.election.biz.ElectionBiz;
import com.cjyfff.dq.common.enums.TaskStatus;
import com.cjyfff.dq.common.component.AcceptTaskComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 当选为master，在宣告选举完成之前需要先进行的业务逻辑
 * 目前需要进行的处理有：
 * 1、数据库中的重新分片
 * 2、把数据库中，“队列中”状态，自己处理的任务放入队列
 * Created by jiashen on 2018/9/9.
 */
@Component
public class MasterBeforeUpdateElectionFinishBiz implements ElectionBiz {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private BizComponent bizComponent;

    @Autowired
    private TransportAction transportAction;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run() {
        logger.info("MasterBeforeElectionFinishBiz begin...");
        acceptTaskComponent.clearQueue();

        List<DelayTask> delayTasks = delayTaskMapper.selectByStatusForUpdate(TaskStatus.ACCEPT.getStatus());

        for (DelayTask delayTask : delayTasks) {
            Byte newShardingId = acceptTaskComponent.getShardingIdByTaskId(delayTask.getTaskId());
            if (! newShardingId.equals(delayTask.getShardingId())) {
                delayTask.setShardingId(newShardingId);
                delayTaskMapper.updateByPrimaryKeySelective(delayTask);
            }
        }

        bizComponent.rePushTaskToQueue();

        transportAction.connectAllNodes();

        logger.info("MasterBeforeElectionFinishBiz end...");
    }

}
