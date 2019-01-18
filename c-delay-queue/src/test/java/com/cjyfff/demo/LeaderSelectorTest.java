package com.cjyfff.demo;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by jiashen on 18-8-15.
 */
public class LeaderSelectorTest {

    private static final String PATH = "/leader";
    private static final int COUNT = 5;

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.43.48:2181",
            new ExponentialBackoffRetry(1000, 3));
        client.start();

        LeaderLatch leaderLatch = new LeaderLatch(client, PATH, "Client #1");

        leaderLatch.addListener(new LeaderLatchListener() {

            @Override
            public void isLeader() {
                System.out.println("I am Leader");

            }

            @Override
            public void notLeader() {
                System.out.println("I am not Leader");

            }
        });


        leaderLatch.start();


        int a = 0;
        while (a < 600) {
            TimeUnit.SECONDS.sleep(1);
            a++;
            if (a % 10 == 0) {
                System.out.println(a);
            }
        }

        leaderLatch.close();
        client.close();

    }
}
