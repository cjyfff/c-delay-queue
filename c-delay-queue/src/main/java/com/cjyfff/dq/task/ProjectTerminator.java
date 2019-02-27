package com.cjyfff.dq.task;

import com.cjyfff.dq.task.transport.info.EventLoopGroupInfo;
import com.cjyfff.election.config.ZooKeeperClient;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2019/2/26.
 */
@Component
@Slf4j
public class ProjectTerminator implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private ZooKeeperClient zooKeeperClient;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.debug("Begin to terminate project...");

        new Thread(() -> {
            shutdownEventLoopGroup(EventLoopGroupInfo.serverBoosGroup);
            shutdownEventLoopGroup(EventLoopGroupInfo.serverWorkerGroup);
            shutdownEventLoopGroup(EventLoopGroupInfo.clientNioEventLoopGroup);
        }).start();


        zooKeeperClient.close();
    }

    private void shutdownEventLoopGroup(EventLoopGroup eventLoopGroup) {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
