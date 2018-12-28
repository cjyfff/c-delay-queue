package com.cjyfff.dq.task.common.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;

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
     * @return
     * @throws LockException
     */
    boolean tryLock(CuratorFramework client, String lockPath, String lockKey, Integer seconds) throws LockException;

    /**
     * 用于幂等的锁，不会阻塞等待，获取锁的结果立即返回
     * @param client zk client
     * @param lockPath 锁目录
     * @param lockKey 锁key
     * @return
     * @throws LockException
     */
    boolean idempotentLock(CuratorFramework client, String lockPath, String lockKey) throws LockException;

    /**
     * 尝试解锁，即使遇到异常也不抛出
     * 解锁动作必须基于锁对象，否则有可能会解了别人的锁
     * @param lockPath 锁目录
     * @param lockKey 锁 key
     */
    void tryUnlock(String lockPath, String lockKey);

    /**
     * 获取 lock key
     * @param lockPath 锁目录
     * @param lockKey 锁 key
     * @return
     */
    String getKeyLockKey(String lockPath, String lockKey);
}
