package com.cjyfff.lock;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.cjyfff.TestConfig;
import com.cjyfff.dq.DelayQueueApplication;
import com.cjyfff.dq.common.lock.ZkLockImpl.LockObject;
import com.cjyfff.election.config.ZooKeeperClient;
import com.cjyfff.dq.common.lock.ZkLock;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by jiashen on 18-11-1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("src/main/resources")
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(
    locations = {"classpath:application.properties"}
)
@SpringBootTest(classes = DelayQueueApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class LockTests {

    @Autowired
    private ZkLock zkLock;

    @Autowired
    private ZooKeeperClient zooKeeperClient;

    @Test
    public void testLock() {
        try {

            String lockKey = "test_lock";

            String lockPath = "/test_lock_path";

            CuratorFramework client = zooKeeperClient.getClient();

            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("lock-test-pool-%d").build();
            ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 20, 10000L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(20),
                namedThreadFactory,
                (r, executor) -> {
                    try {
                        if (!executor.isShutdown()) {
                            executor.getQueue().put(r);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

            // 使用非线程安全的类，进行非线程安全的操作
            final Integer[] a = {0};
            AtomicInteger standardCount = new AtomicInteger(0);

            for (int i = 0; i < 5; i++) {
                pool.submit(() -> {
                    for (int j = 0; j < 100; j++) {
                        try {
                            // 注意此处的 tryLock 是阻塞的，测试代码运行的时间在60秒内，因此 a 可以等于500
                            // 假如是非阻塞或阻塞时间很短的话，a 就不等于500
                            LockObject lockObject = zkLock.tryLock(client, lockPath, lockKey, 60);
                            if (! lockObject.isLockSuccess()) {
                                continue;
                            }
                            a[0]++;
                            standardCount.getAndIncrement();
                            zkLock.tryUnlock(lockObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            pool.shutdown();

            while (! pool.isTerminated()) {
                TimeUnit.SECONDS.sleep(1);
            }

            System.out.println("a: " + a[0]);

            Assert.assertEquals(standardCount.get(), (int)a[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
