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
public class QueueTaskConsumer {

    @Autowired
    private DelayTaskQueue delayTaskQueue;

    @Autowired
    private AsyncQueueTaskConsumer asyncQueueTaskConsumer;

    /**
     * delay queue consumer
     * 对于这个consumer，实际只需要一个线程，
     * 队列非空时，take方法会阻塞直到队列第一个元素的时间达到，
     * 加入阻塞期间有一个时间更短的元素插入，队列会自动消费更前的那个元素。
     * @throws Exception
     */
    @Async
    public void consumer() {
        // 这里必须用死循环来取元素，
        // 假如通过Scheduled来控制取元素频率的话
        // 两个任务的时间相隔小于Scheduled delay time时，也会变为相隔Scheduled delay time
        for (;;) {
            try {
                QueueTask task = delayTaskQueue.queue.take();

                try {
                    log.info(String.format("task %s begin", task.getTaskId()));
                    asyncQueueTaskConsumer.doConsumer(task);
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
