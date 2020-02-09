package com.cjyfff.dq.common.lock;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by jiashen on 2018/10/30.
 */
@Component
@Slf4j
public class ZkLockImpl implements ZkLock {

    private final static String DEFAULT_LOCK_PATH = "/task_lock";

    private final static String DEFAULT_LOCK_KEY = "/default_lock_key";

    @Override
    public LockObject tryLock(CuratorFramework client, String lockPath, String lockKey, Integer seconds) throws LockException {

        String k = getKeyLockKey(lockPath, lockKey);

        try {
            if (StringUtils.isEmpty(k)) {
                return new LockObject(null, false);
            }

            InterProcessLock lock = new MyInterProcessSemaphoreMutex(client, k);
            if (lock.acquire(seconds, TimeUnit.SECONDS)) {
                return new LockObject(lock, true);
            }
            return new LockObject(lock, false);
        } catch (Exception e) {
            log.error("ZkLock tryLock method get error.", e);
            // 此处不能return false;，否则的话出现异常，调用方会误以为是加锁失败
            throw new LockException(e.getMessage());
        }
    }

    @Override
    public LockObject idempotentLock(CuratorFramework client, String lockPath, String lockKey) throws LockException {
        return this.tryLock(client, lockPath, lockKey, 0);
    }

    @Override
    public void tryUnlock(LockObject lockObject) {

        if (lockObject == null) {
            log.warn("lockObject is null.");
            return;
        }

        if (! lockObject.isLockSuccess()) {
            log.warn("lockObject.isLockSuccess() is false.");
        }

        if (lockObject.getLock() == null) {
            log.warn("lockObject.getLock() is null.");
            return;
        }

        InterProcessLock lock = lockObject.getLock();

        try {
            lock.release();

        } catch (Exception e) {
            log.error("ZkLock tryUnlock method get error.", e);
        }
    }

    @Override
    public String getKeyLockKey(String lockPath, String lockKey) {
        if (StringUtils.isEmpty(lockPath)) {
            lockPath = DEFAULT_LOCK_PATH;
        }

        if (StringUtils.isEmpty(lockKey)) {
            log.warn("lock key should not be null.");
            lockKey = DEFAULT_LOCK_KEY;
        }
        return lockPath + "/" + lockKey;
    }

    @Getter
    @Setter
    public static class LockObject {

        public LockObject(InterProcessLock lock, boolean lockSuccess) {
            this.lock = lock;
            this.lockSuccess = lockSuccess;
        }

        private InterProcessLock lock;

        private boolean lockSuccess;
    }
}
