package com.cjyfff.dq.task.service.impl;

import com.cjyfff.dq.common.TaskHandlerContext;
import com.cjyfff.dq.task.handler.ITaskHandler;
import com.cjyfff.dq.task.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 18-10-9.
 */
@Service
@Slf4j
public class TestServiceImpl implements TestService {
    @Autowired
    private TaskHandlerContext taskHandlerContext;

    @Override
    public void test() {
        ITaskHandler exampleHandler = taskHandlerContext.getTaskHandler("exampleHandler");
        if (exampleHandler == null) {
            log.info("No handler found!");
        } else {
            exampleHandler.run("hello world!");
        }
    }
}
