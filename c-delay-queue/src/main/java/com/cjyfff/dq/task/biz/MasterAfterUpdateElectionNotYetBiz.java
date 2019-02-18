package com.cjyfff.dq.task.biz;

import com.cjyfff.dq.task.transport.action.TransportAction;
import com.cjyfff.election.biz.ElectionBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 19-2-15.
 */
@Component
@Slf4j
public class MasterAfterUpdateElectionNotYetBiz implements ElectionBiz {

    @Autowired
    private TransportAction transportAction;

    @Override
    public void run() {
        log.info("MasterAfterUpdateElectionNotYetBiz begin...");
        try {
            transportAction.disconnectAllNodes();
        } catch (Exception e) {
            log.error("MasterAfterUpdateElectionNotYetBiz get error: ", e);
        }

        log.info("MasterAfterUpdateElectionNotYetBiz end...");
    }
}
