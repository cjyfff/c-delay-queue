package com.cjyfff.demo;

import java.util.concurrent.TimeUnit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by jiashen on 2018/10/30.
 */
public class DistributedLockTest {

    public static void main(String[] args) throws Exception {

        final String lockPath = "/lock";

        String connectString = "192.168.43.73:2181";

        RetryPolicy retry = new ExponentialBackoffRetry(1000, 3);

        CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, 60000, 15000, retry);

        CuratorFramework client2 = CuratorFrameworkFactory.newClient(connectString, 60000, 15000, retry);

        client.start();
        client2.start();

        InterProcessLock lock = new InterProcessSemaphoreMutex(client, lockPath);
        InterProcessLock lock2 = new InterProcessSemaphoreMutex(client2, lockPath);

        // 获取锁对象
        lock.acquire();

        // 测试是否可以重入
        // 超时获取锁对象(第一个参数为时间, 第二个参数为时间单位), 因为锁已经被获取, 所以返回 false
        System.out.println("111:" + lock.acquire(20, TimeUnit.SECONDS));
        // 释放锁
        lock.release();

        // lock2 尝试获取锁成功, 因为锁已经被释放
        System.out.println("222:" + lock2.acquire(20, TimeUnit.SECONDS));
        lock2.release();


        client.close();
        client2.close();
    }


}
