package com.cjyfff.dq.task.transport.handler.client;

import com.cjyfff.dq.task.transport.protocol.ClientInitInfoPacket;
import com.cjyfff.dq.task.transport.protocol.PacketType;
import com.cjyfff.election.core.info.ShardingInfo;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jiashen on 19-1-31.
 */
@Slf4j
@Sharable
public class ClientInitHandler extends ChannelInboundHandlerAdapter {

    public static final ClientInitHandler INSTANCE = new ClientInitHandler();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("Client sending init info...");

        ClientInitInfoPacket packet = new ClientInitInfoPacket();

        packet.setShardingId(ShardingInfo.getShardingId());
        packet.setType(PacketType.CLIENT_INIT_INFO);

        ctx.channel().writeAndFlush(packet);
    }
}
