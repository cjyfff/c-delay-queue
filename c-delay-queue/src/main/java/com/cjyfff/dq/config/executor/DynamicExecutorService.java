package com.cjyfff.dq.config.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class DynamicExecutorService {
    public void changeConfig(Integer type, Integer corePoolSize, Integer maxPoolSize, Integer queueCapacity) {
        checkParams(corePoolSize, maxPoolSize, queueCapacity);

        ThreadPoolTaskExecutor executor = DynamicExecutorTypeSelector.getExecutorByType(type);
        if (executor == null) {
            throw new IllegalArgumentException("Illegal type");
        }

        // reference form https://www.cnblogs.com/thisiswhy/p/12690630.html, should configure MaxPoolSize first
        if (maxPoolSize != null) {
            executor.setMaxPoolSize(maxPoolSize);
        }

        if (corePoolSize != null) {
            executor.setCorePoolSize(corePoolSize);
        }

        if (queueCapacity != null) {
            executor.setQueueCapacity(queueCapacity);
        }

    }

    private void checkParams(Integer corePoolSize, Integer maxPoolSize, Integer queueCapacity) {
        if (corePoolSize != null && corePoolSize <= 0) {
            throw new IllegalArgumentException("Illegal corePoolSize");
        }
        if (maxPoolSize != null && maxPoolSize <= 0) {
            throw new IllegalArgumentException("Illegal maxPoolSize");
        }
        if (queueCapacity != null && queueCapacity <= 0) {
            throw new IllegalArgumentException("Illegal queueCapacity");
        }
    }
}
