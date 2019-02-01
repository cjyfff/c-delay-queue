package com.cjyfff.dq.task.transport.action;

import java.util.Date;

import com.cjyfff.election.core.info.ShardingInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jiashen on 19-1-31.
 */
@Slf4j
@Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {

    public static final ClientHandler INSTANCE = new ClientHandler();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info(new Date() + ": 客户端写出数据");

        byte[] bytes = new byte[1];
        bytes[0] = ShardingInfo.getNodeId();

        ByteBuf buffer = ctx.alloc().buffer();

        buffer.writeBytes(bytes);

        ctx.channel().writeAndFlush(buffer);
    }
}
