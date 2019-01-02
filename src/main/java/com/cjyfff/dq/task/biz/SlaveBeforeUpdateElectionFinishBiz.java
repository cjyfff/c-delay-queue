package com.cjyfff.dq.task.biz;

import com.cjyfff.election.biz.NoneBiz;
import com.cjyfff.dq.common.component.AcceptTaskComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 宣告选举完成前的操作：
 * 1、把数据库中，“队列中”状态，自己处理的任务放入队列
 * Created by jiashen on 18-12-11.
 */
@Component
@Slf4j
public class SlaveBeforeUpdateElectionFinishBiz extends NoneBiz{

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private BizComponent bizComponent;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run() {
        log.info("SlaveBeforeUpdateElectionFinishBiz begin...");
        acceptTaskComponent.clearQueue();

        bizComponent.rePushTaskToQueue();

        log.info("SlaveBeforeUpdateElectionFinishBiz end...");
    }
}
