package com.cjyfff.dq.task.service;

import com.cjyfff.dq.task.vo.dto.InnerMsgDto;

/**
 * Created by jiashen on 18-8-17.
 */
public interface InnerMsgService {
    void acceptMsg(InnerMsgDto reqDto) throws Exception;
}
