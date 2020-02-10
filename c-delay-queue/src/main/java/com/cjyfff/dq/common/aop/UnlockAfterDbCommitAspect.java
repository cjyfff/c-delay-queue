package com.cjyfff.dq.common.aop;

import java.util.Map;
import java.util.Map.Entry;

import com.cjyfff.dq.common.lock.UnlockAfterDbCommitInfoHolder;
import com.cjyfff.dq.common.lock.UnlockAfterDbCommitInfoHolder.UnlockAfterDbCommitInfo;
import com.cjyfff.dq.common.lock.ZkLock;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;
/**
 * Created by jiashen on 18-12-14.
 */
@Aspect
@Order(1)
@Component
public class UnlockAfterDbCommitAspect {

    @Autowired
    private ZkLock zkLock;

    @After("@annotation(unlockAfterDbCommit)")
    public void doUnlockAfterDbCommit(JoinPoint point, UnlockAfterDbCommit unlockAfterDbCommit) {
        Map<String, UnlockAfterDbCommitInfo> unlockInfoMap = UnlockAfterDbCommitInfoHolder.getAllInfoMap();
        if (unlockInfoMap == null) {
            return;
        }

        for (Entry entry : unlockInfoMap.entrySet()) {
            String k = (String) entry.getKey();
            UnlockAfterDbCommitInfo info = (UnlockAfterDbCommitInfo) entry.getValue();
            if (info.isNeedUnlock()) {
                zkLock.tryUnlock(info.getLockObject());
            }

            UnlockAfterDbCommitInfoHolder.removeInfoByKey(k);
        }
    }
}
