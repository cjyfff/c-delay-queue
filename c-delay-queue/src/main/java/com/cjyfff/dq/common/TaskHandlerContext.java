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

    private static ConcurrentHashMap<String, ITaskHandler> taskHandlerMap = new ConcurrentHashMap<>();

    /**
    Spring 会在bean初始化后调用 ApplicationContextAware 的 setApplicationContext，把 applicationContext 传入
    **/
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
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

    public ITaskHandler getTaskHandler(String name){
        return taskHandlerMap.get(name);
    }
}
