package com.cjyfff.dq.task.transport.handler.server;

import java.util.Date;

import com.cjyfff.dq.task.transport.protocol.TaskTransportRespPacket;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jiashen on 2019/2/11.
 */
@Slf4j
@Sharable
public class ServerTransportTaskRespHandler extends SimpleChannelInboundHandler<TaskTransportRespPacket> {
    public static final ServerTransportTaskRespHandler INSTANCE = new ServerTransportTaskRespHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TaskTransportRespPacket respPacket) {
        log.info(new Date() + ": 服务端resp handler读到数据 -> " + respPacket.getResult());
    }
}
