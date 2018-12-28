package com.cjyfff.dq.task.controller;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * Created by jiashen on 18-11-1.
 */
public class BaseController {
    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
}
