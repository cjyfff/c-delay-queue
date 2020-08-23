package com.cjyfff.dq.common.component;

import java.util.Map;

import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.election.core.info.ElectionStatus;
import com.cjyfff.election.core.info.ElectionStatus.ElectionStatusType;
import com.cjyfff.election.core.info.ShardingInfo;
import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.task.queue.QueueInternalTask;
import com.cjyfff.dq.task.queue.TaskQueue;
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
    private TaskQueue taskQueue;

    @Value("${delay_queue.critical_polling_time}")
    private Long criticalPollingTime;

    /**
     * 检查选举状态
     * @throws ApiException
     */
    public void checkElectionStatus() throws ApiException {
        if (! ElectionStatusType.FINISH.equals(ElectionStatus.getElectionStatus())) {
            throw new ApiException(ErrorCodeMsg.ELECTION_NOT_FINISHED_CODE, ErrorCodeMsg.ELECTION_NOT_FINISHED_MSG);
        }
    }

    /**
     * 根据 task id 算出分片 id
     * @param taskId
     * @return
     */
    public Integer getShardingIdByTaskId(String taskId) {
        Map<Integer, String> shardingMap = ShardingInfo.getShardingMap();

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
        return getShardingIdByTaskId(taskId).equals(ShardingInfo.getShardingId());
    }

    /**
     * 把任务放到延时队列
     * @param task
     */
    public void pushToQueue(QueueInternalTask task) {
        taskQueue.queue.add(task);
    }

    /**
     * 清空任务队列
     */
    public void clearQueue() {
        log.warn("Prepare to clear the task queue...");
        taskQueue.queue.clear();
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
