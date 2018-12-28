package com.cjyfff.dq.task.common.component;

import java.util.Map;

import com.cjyfff.election.info.ElectionStatus;
import com.cjyfff.election.info.ElectionStatus.ElectionStatusType;
import com.cjyfff.election.info.ShardingInfo;
import com.cjyfff.dq.task.common.ApiException;
import com.cjyfff.dq.task.queue.QueueTask;
import com.cjyfff.dq.task.queue.DelayTaskQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/9/24.
 */
@Component
@Slf4j
public class AcceptTaskComponent {

    @Autowired
    private ElectionStatus electionStatus;

    @Autowired
    private ShardingInfo shardingInfo;

    @Autowired
    private DelayTaskQueue delayTaskQueue;

    @Value("${delay_queue.critical_polling_time}")
    private Long criticalPollingTime;

    /**
     * 检查选举状态
     * @throws ApiException
     */
    public void checkElectionStatus() throws ApiException {
        if (! ElectionStatusType.FINISH.equals(electionStatus.getElectionStatus())) {
            throw new ApiException("101", "选举未完成，不接受请求");
        }
    }

    /**
     * 根据 task id 算出分片 id
     * @param taskId
     * @return
     */
    public Integer getShardingIdByTaskId(String taskId) {
        Map<Integer, String> shardingMap = shardingInfo.getShardingMap();

        // 分片数量
        int shardingAmount = shardingMap.size();

        // hashCode有可能是负数，需要处理一下
        return (taskId.hashCode() & 0x7FFFFFFF) % shardingAmount;
    }

    /**
     * 根据task id 判断任务是否自己处理
     * @param taskId
     * @return
     */
    public boolean checkIsMyTask(String taskId) {
        return getShardingIdByTaskId(taskId).equals(shardingInfo.getNodeId());
    }

    /**
     * 把任务放到延时队列
     * @param task
     */
    public void pushToQueue(QueueTask task) {
        delayTaskQueue.queue.add(task);
    }

    /**
     * 清空任务队列
     */
    public void clearQueue() {
        log.warn("Prepare to clear the task queue...");
        delayTaskQueue.queue.clear();
    }

    /**
     * 根据入参中的delayTime判断任务是不是需要马上入队
     * @param delayTime
     * @return
     */
    public boolean checkNeedToPushQueueNow(Long delayTime) {
        return delayTime.compareTo(criticalPollingTime) <= 0;
    }
}
