package com.cjyfff.dq.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cjyfff.dq.task.handler.ITaskHandler;
import com.cjyfff.dq.task.handler.annotation.TaskHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 18-10-8.
 */
@Component
public class TaskHandlerContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    private static ConcurrentHashMap<String, ITaskHandler> taskHandlerMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        TaskHandlerContext.applicationContext = applicationContext;

        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(TaskHandler.class);

        if (serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                if (serviceBean instanceof ITaskHandler){
                    String name = serviceBean.getClass().getAnnotation(TaskHandler.class).value();
                    ITaskHandler handler = (ITaskHandler) serviceBean;

                    taskHandlerMap.put(name, handler);
                }
            }
        }
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ITaskHandler getTaskHandler(String name){
        return taskHandlerMap.get(name);
    }
}
