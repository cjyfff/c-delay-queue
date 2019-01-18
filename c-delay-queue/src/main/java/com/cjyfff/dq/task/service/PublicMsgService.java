package com.cjyfff.dq.task.service;

import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;

/**
 * Created by jiashen on 18-8-17.
 */
public interface PublicMsgService {

    void acceptMsg(AcceptMsgDto reqDto) throws Exception;
}
