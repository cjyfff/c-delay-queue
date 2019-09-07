package com.cjyfff.dq.common.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cjyfff.dq.common.lock.ZkLockImpl.LockObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * Created by jiashen on 18-12-14.
 */
public class UnlockAfterDbCommitInfoHolder {

    /**
     * 每一个任务，在LOCK_KEY_SET中都会对应一个entity，直到该任务执行完毕才会删除这个entity，
     * 因此LOCK_KEY_SET的初始容量要设置大一点
     */
    private static Map<String, UnlockAfterDbCommitInfo> infoMap = new ConcurrentHashMap<>(1366);

    public static void setInfo2Holder(LockObject lockObject, String lockKey, boolean needUnlock) {

        UnlockAfterDbCommitInfo info = new UnlockAfterDbCommitInfo();
        info.setNeedUnlock(needUnlock);
        info.setLockKey(lockKey);
        info.setLockObject(lockObject);
        infoMap.put(lockKey, info);
    }

    public static void setInfo2Holder(LockObject lockObject, String lockKey) {
        if (lockObject != null && lockObject.isLockSuccess()) {
            setInfo2Holder(lockObject, lockKey, true);
        }
    }

    /**
     * 根据 k 获取 HOLDER 中对应的 item
     * @param k 锁目录 + 锁key
     * @return
     */
    public static UnlockAfterDbCommitInfo getInfoByKey(String k) {
        if (StringUtils.isEmpty(k)) {
            throw new IllegalArgumentException("Parameters key and lock can not be null");
        }

        return infoMap.get(k);
    }

    public static Map<String, UnlockAfterDbCommitInfo> getAllInfoMap() {
        return infoMap;
    }

    /**
     * HOLDER中删除k对应的item
     * @param k 锁目录 + 锁key
     */
    public static void removeInfoByKey(String k) {
        infoMap.remove(k);
    }

    @Getter
    @Setter
    public static class UnlockAfterDbCommitInfo {
        private boolean needUnlock = true;

        private String lockKey;

        private LockObject lockObject;
    }
}
