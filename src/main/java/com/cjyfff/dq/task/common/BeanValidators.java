package com.cjyfff.dq.task.common;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * Created by jiashen on 18-11-1.
 */
public class BeanValidators {
    @SuppressWarnings("unchecked")
    public static void validateWithParameterException(Validator validator, Object object, Class<?>... groups) throws ApiException {
        Set constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            String message =  (String)constraintViolations.stream().map(cv -> {
                ConstraintViolation constraintViolation = (ConstraintViolation)cv;
                return
                    "[" + constraintViolation.getPropertyPath().toString() +"]"+ constraintViolation.getMessageTemplate();
            }).collect(Collectors.joining(","));
            throw new ApiException("-888", "Parameters validate get error:" + message);
        }
    }
}
