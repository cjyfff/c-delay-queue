package com.cjyfff.dq.task.queue;

import java.util.concurrent.DelayQueue;

import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/10/3.
 */
@Component(value = "delayTaskQueue")
public class DelayTaskQueue {
    public final DelayQueue<QueueTask> queue = new DelayQueue<>();
}
