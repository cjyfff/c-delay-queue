package com.cjyfff.dq.task.transport.action;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;

import com.cjyfff.dq.common.error.ApiException;
import com.cjyfff.dq.common.error.ErrorCodeMsg;
import com.cjyfff.dq.task.transport.handler.PacketEncoder;
import com.cjyfff.dq.task.transport.handler.Spliter;
import com.cjyfff.dq.task.transport.handler.client.ClientInitHandler;
import com.cjyfff.dq.task.transport.handler.PacketDecoder;
import com.cjyfff.dq.task.transport.handler.client.ClientTransportTaskReqHandler;
import com.cjyfff.dq.task.transport.handler.client.ClientTransportTaskRespHandler;
import com.cjyfff.dq.task.transport.info.EventLoopGroupInfo;
import com.cjyfff.dq.task.transport.info.NodeChannelInfo;
import com.cjyfff.dq.task.transport.info.NodeChannelInfo.OneNodeChannelInfo;
import com.cjyfff.dq.task.transport.protocol.Packet;
import com.cjyfff.election.core.info.ShardingInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 19-1-31.
 */
@Slf4j
@Component
public class TransportAction {

    @Value("${l_election.specified_port}")
    private int transportPort;

    public void connectAllNodes() {

        int shardingSize = ShardingInfo.getShardingMap().size();
        if (shardingSize <= 1) {
            return;
        }

        Integer myShardingId = ShardingInfo.getShardingId();

        for (Entry<Integer, String> info : ShardingInfo.getShardingMap().entrySet()) {

            if (info.getKey() < myShardingId) {
                // 连接其他节点netty服务
                String serverHost = info.getValue().split(":")[0];
                int serverPort = Integer.valueOf(info.getValue().split(":")[1]);
                try {
                    connectServer(info.getKey(), serverHost, serverPort);
                } catch (Exception e) {
                    log.error("Connect to node method get error:", e);
                }

            }
        }

    }

    public void disconnectAllNodes() throws Exception {
        int shardingSize = ShardingInfo.getShardingMap().size();
        if (shardingSize <= 1) {
            return;
        }

        for (Entry<Integer, OneNodeChannelInfo> info : NodeChannelInfo.channelInfoMap.entrySet()) {
            info.getValue().getChannel().closeFuture();
        }
    }

    public void sendMsg(Integer shardingId, Packet packet) throws ApiException{
        checkSendPacket(packet);

        OneNodeChannelInfo nodeChannelInfo = NodeChannelInfo.channelInfoMap.get(shardingId);
        if (nodeChannelInfo == null) {
            throw new ApiException(ErrorCodeMsg.CAN_NOT_GET_SHARDING_INFO_CODE, ErrorCodeMsg.CAN_NOT_GET_SHARDING_INFO_MSG);
        }

        nodeChannelInfo.getChannel().writeAndFlush(packet).addListener(future -> {
            if (future.isSuccess()) {
                log.debug("Success to send Msg, target sharding id: {}", shardingId);
            } else {
                String packetStr = JSON.toJSONString(packet);
                log.error("Fail to send Msg, msg data:{}", packetStr);
            }
        });
    }

    private void checkSendPacket(Packet packet) {
        if (packet.getShardingId() == null || packet.getType() == null) {
            throw new IllegalArgumentException("Pack's sharding id, type can not be null.");
        }
    }

    private void connectServer(Integer serverShardingId, String serverHost, int serverPort) throws Exception {
        EventLoopGroup workerGroup = EventLoopGroupInfo.clientNioEventLoopGroup;

        if (workerGroup == null) {
            throw new ApiException(ErrorCodeMsg.SYSTEM_ERROR_CODE, "Client worker group is not initialize...");
        }

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
                        .addLast(new Spliter())
                        .addLast(new PacketDecoder())
                        .addLast(ClientInitHandler.INSTANCE)
                        .addLast(ClientTransportTaskReqHandler.INSTANCE)
                        .addLast(ClientTransportTaskRespHandler.INSTANCE)
                        .addLast(new PacketEncoder());
                }
            });

        doConnect(bootstrap, serverHost, serverPort, 5, serverShardingId);
    }

    private static void doConnect(Bootstrap bootstrap, String host, int port, int retry, Integer serverShardingId) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                Channel channel = ((ChannelFuture) future).channel();

                NodeChannelInfo.channelInfoMap.put(serverShardingId, new OneNodeChannelInfo(channel, true));

                log.info("Success to connect server {}", host);
            } else if (retry == 0) {
                log.error("Fail to connect server {}", host);
            } else {
                int order = (5 - retry) + 1;
                int delay = 1 << order;
                log.error("fail to connect，retry {} times...", order);
                bootstrap.config().group().schedule(() -> doConnect(bootstrap, host, port, retry - 1, serverShardingId), delay, TimeUnit
                    .SECONDS);
            }
        });
    }
}
