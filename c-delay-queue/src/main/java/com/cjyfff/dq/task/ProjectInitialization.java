package com.cjyfff.dq.task;

import com.cjyfff.dq.task.queue.TaskTaker;
import com.cjyfff.dq.task.transport.action.TransportInitAction;
import com.cjyfff.election.core.Election;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/10/3.
 */
@Component
@Slf4j
public class ProjectInitialization implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private Election election;

    @Autowired
    private TaskTaker taskTaker;

    @Autowired
    private TransportInitAction transportInitAction;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.debug("Starting init method...");
        election.start();

        taskTaker.takeTask();

        transportInitAction.startServer();
    }
}
