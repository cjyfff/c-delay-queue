package com.cjyfff.dq.task.transport.handler.server;

import com.cjyfff.dq.task.transport.info.NodeChannelInfo;
import com.cjyfff.dq.task.transport.info.NodeChannelInfo.OneNodeChannelInfo;
import com.cjyfff.dq.task.transport.protocol.ClientInitInfoPacket;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jiashen on 2019/2/11.
 */
@Slf4j
@Sharable
public class ServerGetClientInitInfoHandler extends SimpleChannelInboundHandler<ClientInitInfoPacket> {
    public static final ServerGetClientInitInfoHandler INSTANCE = new ServerGetClientInitInfoHandler();

    private ServerGetClientInitInfoHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientInitInfoPacket clientInitInfoPacket) {
        log.debug("ServerGetClientInitInfoHandler get msg，sharding id: {}", clientInitInfoPacket.getShardingId());

        NodeChannelInfo.channelInfoMap.put(clientInitInfoPacket.getShardingId(),
            new OneNodeChannelInfo(ctx.channel(), false));
    }
}
