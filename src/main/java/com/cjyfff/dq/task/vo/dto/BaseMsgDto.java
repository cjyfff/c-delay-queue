package com.cjyfff.dq.task.vo.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 18-12-26.
 */
@Getter
@Setter
public class BaseMsgDto {
    /**
     * taskId，需要保证唯一
     */
    @NotEmpty(message = "can not be empty")
    private String taskId;

    /**
     * 调用方法名
     */
    @NotEmpty(message = "can not be empty")
    private String functionName;

    /**
     * 延时时间，单位秒
     */
    @NotNull(message = "can not be null")
    private Long delayTime;

    /**
     * 随机字符串，用于保证接口幂等
     */
    @NotNull(message = "can not be null")
    private String nonceStr;

    /**
     * 调用参数（json字符串）
     */
    private String params;

    /**
     * 重试次数
     */
    @Min(0)
    @Max(10)
    private Byte retryCount;

    /**
     * 重试周期，单位秒
     */
    @Min(1)
    @Max(60)
    private Integer retryInterval;
}
