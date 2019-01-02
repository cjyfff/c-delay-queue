package com.cjyfff.dq.common.lock;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.Lease;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.springframework.util.StringUtils;

/**
 * curator的InterProcessLock实现，
 * 在锁目录建立时，假如zk不支持container模式的话，目录会设置为persistent模式
 * 锁释放时不会删除这些目录，会造成性能问题
 * 因此自己实现InterProcessLock逻辑
 * Created by jiashen on 18-12-17.
 */
public class MyInterProcessSemaphoreMutex implements InterProcessLock {

    private CuratorFramework client;

    private String path;

    private volatile Lease lease;

    public MyInterProcessSemaphoreMutex(CuratorFramework client, String path) {
        this.client = client;
        this.path = path;
    }


    @Override
    public void acquire() throws Exception {
        checkPath();

        for (;;) {
            try {
                client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL).forPath(path);
                this.lease = getNewLease();
                break;
            } catch (NodeExistsException le) {

            }
        }
    }

    @Override
    public boolean acquire(long timeOut, TimeUnit timeUnit) throws Exception {
        checkPath();

        Long expertTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeOut, timeUnit);

        for (;;) {
            try {
                client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL).forPath(path);
                this.lease = getNewLease();
                return true;
            } catch (NodeExistsException le) {

            }

            if (System.currentTimeMillis() >= expertTime) {
                return false;
            }
        }
    }

    @Override
    public void release() throws Exception {
        checkPath();

        client.delete().forPath(path);
        this.lease = null;
    }

    @Override
    public boolean isAcquiredInThisProcess() {
        return this.lease != null;
    }

    private void checkPath() {
        if (StringUtils.isEmpty(this.path) || ! this.path.startsWith("/")) {
            throw new IllegalArgumentException("path must start whit '/'.");
        }
    }

    private Lease getNewLease() {
        return new Lease() {
            @Override
            public void close() throws IOException {

            }

            @Override
            public byte[] getData() throws Exception {
                return new byte[0];
            }

            @Override
            public String getNodeName() {
                return null;
            }
        };
    }
}
