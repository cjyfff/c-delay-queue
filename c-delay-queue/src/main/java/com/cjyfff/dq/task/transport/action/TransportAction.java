package com.cjyfff.dq.task.transport.action;

import java.util.Map.Entry;

import com.cjyfff.dq.task.transport.info.NodeChannelInfo;
import com.cjyfff.dq.task.transport.info.NodeChannelInfo.OneNodeChannelInfo;
import com.cjyfff.dq.task.transport.protocol.Packet;
import com.cjyfff.election.core.info.ShardingInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 19-1-31.
 */
@Slf4j
@Component
public class TransportAction {

    @Autowired
    private ShardingInfo shardingInfo;

    // todo: 需要更换为l_election.specified_port
    @Value("${transport.port}")
    private int transportPort;


    public void connectAllNodes() {

        int shardingSize = shardingInfo.getShardingMap().size();
        if (shardingSize <= 1) {
            return;
        }

        Byte myNodeId = shardingInfo.getNodeId();

        int i = 0;
        for (Entry<Byte, String> info : shardingInfo.getShardingMap().entrySet()) {

            if (info.getKey() < myNodeId) {
                // 连接其他节点netty服务
                String serverHost = info.getValue().split(":")[0];
                int serverIp = 9999;
                connectServer(info.getKey(), serverHost, serverIp);

            } else if (info.getKey().equals(myNodeId)) {

                // 节点是最后一个节点时不需要创建server
                if (i != shardingSize - 1) {
                    // 创建server供其他节点连接
                    createServer();
                    break;
                }


            }

            i ++;
        }

    }

    public void disconnectAllNodes() throws Exception {
        int shardingSize = shardingInfo.getShardingMap().size();
        if (shardingSize <= 1) {
            return;
        }

        for (Entry<Byte, OneNodeChannelInfo> info : NodeChannelInfo.channelInfoMap.entrySet()) {
            info.getValue().getChannel().closeFuture().sync();
        }
    }

    public void sendMsg(Byte nodeId, Packet packet) {}

    private void createServer() {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
            .group(boosGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) {
                    ch.pipeline().addLast(new ServerHandler());
                }
            });


        serverBootstrap.bind(transportPort).addListener(future -> {
            if (future.isSuccess()) {
                log.info("Success to create netty server on %s port", transportPort);
            } else {
                log.info("Fail to create netty server on %s port", transportPort);
            }
        });
    }

    private void connectServer(Byte serverNodeId, String serverHost, int serverIp) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
            .group(workerGroup)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ClientHandler());
                }
            });

        bootstrap.connect(serverHost, serverIp).addListener(future -> {
            if (future.isSuccess()) {

                Channel channel = ((ChannelFuture) future).channel();

                NodeChannelInfo.channelInfoMap.put(serverNodeId, new OneNodeChannelInfo(channel, false));

                log.info("Success to connect server %s", serverHost);
            } else {
                log.error("Fail to connect server %s", serverHost);
            }
        });
    }
}
