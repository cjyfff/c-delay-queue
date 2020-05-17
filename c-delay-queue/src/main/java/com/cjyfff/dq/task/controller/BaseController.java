package com.cjyfff.dq.task.controller;

import javax.validation.Validation;
import javax.validation.Validator;

import com.cjyfff.dq.common.BeanValidators;
import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.task.vo.dto.BaseMsgDto;

/**
 * Created by jiashen on 18-11-1.
 */
public class BaseController {
    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public void checkAccessMsgParams(BaseMsgDto reqDto) throws ApiException {
        BeanValidators.validateWithParameterException(validator, reqDto);

        if (reqDto.getRetryCount() == null) {
            reqDto.setRetryCount(Byte.valueOf("0"));
        }

        if (reqDto.getRetryInterval() == null) {
            reqDto.setRetryInterval(1);
        }
    }
}
