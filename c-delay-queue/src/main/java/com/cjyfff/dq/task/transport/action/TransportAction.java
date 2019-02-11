package com.cjyfff.dq.task.transport.action;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.task.transport.handler.PacketEncoder;
import com.cjyfff.dq.task.transport.handler.client.ClientInitHandler;
import com.cjyfff.dq.task.transport.handler.PacketDecoder;
import com.cjyfff.dq.task.transport.handler.client.ClientTransportTaskReqHandler;
import com.cjyfff.dq.task.transport.handler.client.ClientTransportTaskRespHandler;
import com.cjyfff.dq.task.transport.handler.server.ServerGetClientInitInfoHandler;
import com.cjyfff.dq.task.transport.handler.server.ServerTransportTaskReqHandler;
import com.cjyfff.dq.task.transport.handler.server.ServerTransportTaskRespHandler;
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
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 19-1-31.
 */
@Slf4j
@Component
public class TransportAction {

    // todo: 需要更换为l_election.specified_port
    @Value("${transport.port}")
    private int transportPort;

    public void connectAllNodes() {

        int shardingSize = ShardingInfo.getShardingMap().size();
        if (shardingSize <= 1) {
            return;
        }

        Byte myNodeId = ShardingInfo.getNodeId();

        int i = 0;
        for (Entry<Byte, String> info : ShardingInfo.getShardingMap().entrySet()) {

            if (info.getKey() < myNodeId) {
                // 连接其他节点netty服务
                String serverHost = info.getValue().split(":")[0];
                int serverPort = 9999;
                asClient(info.getKey(), serverHost, serverPort);

            } else if (info.getKey().equals(myNodeId)) {

                // 节点是最后一个节点时不需要创建server
                if (i != shardingSize - 1) {
                    // 创建server供其他节点连接
                    asServer();
                    break;
                }
            }

            i ++;
        }

    }

    public void disconnectAllNodes() throws Exception {
        int shardingSize = ShardingInfo.getShardingMap().size();
        if (shardingSize <= 1) {
            return;
        }

        for (Entry<Byte, OneNodeChannelInfo> info : NodeChannelInfo.channelInfoMap.entrySet()) {
            info.getValue().getChannel().closeFuture().sync();
        }
    }

    public void sendMsg(Byte nodeId, Packet packet) throws ApiException{
        OneNodeChannelInfo nodeChannelInfo = NodeChannelInfo.channelInfoMap.get(nodeId);
        if (nodeChannelInfo == null) {
            throw new ApiException(ErrorCodeMsg.CAN_NOT_GET_SHARDING_INFO_CODE, ErrorCodeMsg.CAN_NOT_GET_SHARDING_INFO_MSG);
        }

        nodeChannelInfo.getChannel().writeAndFlush(packet).addListener(future -> {
            if (future.isSuccess()) {
                log.debug("Success to send Msg");
            } else {
                log.error("Fail to send Msg");
            }
        });
    }

    private void asServer() {
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
                    ch.pipeline()
                        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 6, 4))
                        .addLast(new PacketDecoder())
                        .addLast(ServerGetClientInitInfoHandler.INSTANCE)
                        .addLast(ServerTransportTaskReqHandler.INSTANCE)
                        .addLast(ServerTransportTaskRespHandler.INSTANCE)
                        .addLast(new PacketEncoder());
                }
            });


        serverBootstrap.bind(transportPort).addListener(future -> {
            if (future.isSuccess()) {
                log.info("Success to create netty server on {} port", transportPort);
            } else {
                log.info("Fail to create netty server on {} port", transportPort);
            }
        });
    }

    private void asClient(Byte serverNodeId, String serverHost, int serverPort) {
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
                    ch.pipeline()
                        .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 6, 4))
                        .addLast(new PacketDecoder())
                        .addLast(ClientInitHandler.INSTANCE)
                        .addLast(ClientTransportTaskReqHandler.INSTANCE)
                        .addLast(ClientTransportTaskRespHandler.INSTANCE)
                        .addLast(new PacketEncoder());
                }
            });

        doConnect(bootstrap, serverHost, serverPort, 5, serverNodeId);
    }

    private static void doConnect(Bootstrap bootstrap, String host, int port, int retry, Byte serverNodeId) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                Channel channel = ((ChannelFuture) future).channel();

                NodeChannelInfo.channelInfoMap.put(serverNodeId, new OneNodeChannelInfo(channel, true));

                log.info("Success to connect server {}", host);
            } else if (retry == 0) {
                log.error("Fail to connect server {}", host);
            } else {
                int order = (5 - retry) + 1;
                int delay = 1 << order;
                log.error("fail to connect，retry {} times...", order);
                bootstrap.config().group().schedule(() -> doConnect(bootstrap, host, port, retry - 1, serverNodeId), delay, TimeUnit
                    .SECONDS);
            }
        });
    }
}
