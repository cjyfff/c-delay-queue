package com.cjyfff.dq.task.transport.handler.client;

import java.util.Date;

import com.cjyfff.dq.task.transport.protocol.TaskTransportRespPacket;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jiashen on 19-2-2.
 */
@Slf4j
@Sharable
public class ClientTransportTaskHandler extends SimpleChannelInboundHandler<TaskTransportRespPacket> {

    public static final ClientTransportTaskHandler INSTANCE = new ClientTransportTaskHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TaskTransportRespPacket respPacket) {
        log.info(new Date() + ": 客户端读到数据 -> " + respPacket.getResult());
    }
}
