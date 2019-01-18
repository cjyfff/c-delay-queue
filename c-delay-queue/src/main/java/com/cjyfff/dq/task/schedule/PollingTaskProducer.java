package com.cjyfff.dq.task.schedule;

import java.util.Date;
import java.util.List;

import com.cjyfff.election.core.info.ElectionStatus;
import com.cjyfff.election.core.info.ElectionStatus.ElectionStatusType;
import com.cjyfff.election.core.info.ShardingInfo;
import com.cjyfff.dq.common.enums.TaskStatus;
import com.cjyfff.dq.common.component.AcceptTaskComponent;
import com.cjyfff.dq.common.component.ExecLogComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.queue.QueueTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2018/10/4.
 * 从数据库中取出任务插入延时队列的定时任务
 */
@Component
@Slf4j
public class PollingTaskProducer {

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private ShardingInfo shardingInfo;

    @Autowired
    private ElectionStatus electionStatus;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private ExecLogComponent execLogComponent;

    @Value("${delay_queue.critical_polling_time}")
    private Long pollingTime;

    @Scheduled(fixedRateString = "#{${delay_queue.critical_polling_time} * 1000}")
    @Transactional(rollbackFor = Exception.class)
    public void run() throws Exception {
        log.info("begin PollingTaskProducer ------");

        if (! ElectionStatusType.FINISH.equals(electionStatus.getElectionStatus())) {
            log.warn("Election not finish, PollingTaskProducer can not process...");
            return;
        }

        Long nowSecond = System.currentTimeMillis() / 1000;
        // 这里不能使用 select for update 修改数据，会锁全表。
        // 考虑到已经使用 sharding id 筛选出自己的数据，sharding id 在选举完成后不会出现同一个id被多个节点分配的问题
        // 唯一可能出现任务被重复消费的情况是，同一个节点上，一次定时任务没跑完，第二次定时任务又开始跑。因为定时任务已经配置为fixedRate，
        // 所以也不会出现这种可能。
        // 因此这里的数据查询不用加锁。
        List<DelayTask> taskList = delayTaskMapper.selectByStatusAndExecuteTime(TaskStatus.POLLING.getStatus(),
            shardingInfo.getNodeId().byteValue(), 0L, nowSecond + pollingTime);

        for (DelayTask delayTask : taskList) {
            QueueTask task = new QueueTask(delayTask.getTaskId(), delayTask.getExecuteTime());
            acceptTaskComponent.pushToQueue(task);

            delayTask.setStatus(TaskStatus.IN_QUEUE.getStatus());
            delayTask.setModifiedAt(new Date());
            delayTaskMapper.updateByPrimaryKeySelective(delayTask);

            execLogComponent.insertLog(delayTask, TaskStatus.IN_QUEUE.getStatus(),
                String.format("polling task in queue: %s", delayTask.getTaskId()));
        }
    }
}
