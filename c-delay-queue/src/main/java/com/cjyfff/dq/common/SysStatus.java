package com.cjyfff.dq.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 18-12-10.
 */
@Component
@Getter
@Setter
public class SysStatus {
    /**
     * 标记系统是不是初始启动（第一次运行或者重启）
     */
    private volatile boolean initCompleted = false;
}
