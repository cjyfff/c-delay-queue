package com.cjyfff.dq.task.handler.impl;

import com.cjyfff.dq.task.handler.HandlerResult;
import com.cjyfff.dq.task.handler.ITaskHandler;
import com.cjyfff.dq.task.handler.annotation.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 18-10-8.
 */
@Slf4j
@Service
@TaskHandler(value = "exampleHandler")
public class ExampleHandler implements ITaskHandler {

    @Override
    public HandlerResult run(String paras) {
        log.info("Run ExampleHandler with paras: " + paras);

        return new HandlerResult(HandlerResult.SUCCESS_CODE, "success");
    }
}
