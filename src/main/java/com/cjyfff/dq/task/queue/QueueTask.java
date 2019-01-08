package com.cjyfff.dq.task.queue;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 2018/10/3.
 */
@Getter
@Setter
public class QueueTask implements Delayed {

    private String taskId;

    public TimeUnit delayTimeUnit;

    public Long executeTime;

    private QueueTask() {}

    public QueueTask(String taskId, Long executeTimeSec) {
        this.taskId = taskId;
        this.delayTimeUnit = TimeUnit.MILLISECONDS;
        this.executeTime = executeTimeSec * 1000;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(executeTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if(this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)) {
            return 1;
        }else if(this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS)) {
            return -1;
        }
        return 0;
    }
}
