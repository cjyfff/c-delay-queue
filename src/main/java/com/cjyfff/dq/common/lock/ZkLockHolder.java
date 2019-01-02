package com.cjyfff.dq.common.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.springframework.util.StringUtils;

/**
 * Created by jiashen on 2018/10/30.
 */
public class ZkLockHolder {

    /**
     * 每一个任务，在LOCK_KEY_SET中都会对应一个entity，直到该任务执行完毕才会删除这个entity，
     * 因此LOCK_KEY_SET的初始容量要设置大一点
     */
    private static Map<String, InterProcessLock> lockMap = new ConcurrentHashMap<>(1366);

    private ZkLockHolder() {}

    public static void setLockKeySet(String key, InterProcessLock lock) {
        if (StringUtils.isEmpty(key) || lock == null) {
            throw new IllegalArgumentException("Parameters key and lock can not be null");
        }

        lockMap.put(key, lock);
    }

    public static InterProcessLock getLockByKey(String k) {
        if (StringUtils.isEmpty(k)) {
            throw new IllegalArgumentException("Parameters k can not be null");
        }

        return lockMap.get(k);
    }

    public static void removeLockByKey(String k) {
        if (StringUtils.isEmpty(k)) {
            throw new IllegalArgumentException("Parameters k can not be null");
        }

        lockMap.remove(k);
    }

}
