package com.cjyfff.dq.task.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/10/3.
 */
@Component
@Slf4j
public class TaskTaker {

    @Autowired
    private TaskQueue taskQueue;

    @Autowired
    private AsyncTaskConsumer asyncTaskConsumer;

    /**
     * 新开一个线程，从 delay queue 取出到达时间的任务放到异步线程池中进行消费
     * @throws Exception
     */
    @Async
    public void takeTask() {
        // 这里必须用死循环来取元素，
        // 假如通过Scheduled来控制取元素频率的话
        // 两个任务的时间相隔小于Scheduled delay time时，也会变为相隔Scheduled delay time
        for (;;) {
            try {
                QueueInternalTask task = taskQueue.queue.take();

                try {
                    log.info(String.format("task %s begin", task.getTaskId()));
                    asyncTaskConsumer.doConsumer(task);
                } catch (Exception e) {
                    // 确保doConsumer的异常不会中断循环
                    log.error("QueueTaskConsumer doConsumer method get error:", e);
                }
            } catch (InterruptedException ie) {
                break;
            }
        }
    }
}
