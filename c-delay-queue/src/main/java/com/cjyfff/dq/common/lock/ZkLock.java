package com.cjyfff.dq.common.lock;

import com.cjyfff.dq.common.lock.ZkLockImpl.LockObject;
import org.apache.curator.framework.CuratorFramework;

/**
 * Created by jiashen on 2018/10/30.
 */
public interface ZkLock {

    /**
     * 尝试获得锁，成功返回true，失败返回false，seconds参数代表尝试获得锁所阻塞等待的时间
     * @param client zk client
     * @param lockPath 锁目录
     * @param lockKey 锁key
     * @param seconds 尝试获得锁所阻塞等待的时间，单位秒
     * @return LockObject 锁对象
     * @throws LockException
     */
    LockObject tryLock(CuratorFramework client, String lockPath, String lockKey, Integer seconds) throws LockException;

    /**
     * 用于幂等的锁，不会阻塞等待，获取锁的结果立即返回
     * @param client zk client
     * @param lockPath 锁目录
     * @param lockKey 锁key
     * @return LockObject 锁对象
     * @throws LockException
     */
    LockObject idempotentLock(CuratorFramework client, String lockPath, String lockKey) throws LockException;

    /**
     * 尝试解锁，即使遇到异常也不抛出
     * 解锁动作必须基于锁对象，否则有可能会解了别人的锁
     * @param lockObject 锁对象
     */
    void tryUnlock(LockObject lockObject);

    /**
     * 获取 lock key
     * @param lockPath 锁目录
     * @param lockKey 锁 key
     * @return
     */
    String getKeyLockKey(String lockPath, String lockKey);
}
