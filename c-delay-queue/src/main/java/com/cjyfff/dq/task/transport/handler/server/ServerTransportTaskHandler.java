package com.cjyfff.dq.task.transport.handler.server;

import com.cjyfff.dq.task.transport.protocol.PacketType;
import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
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
public class ServerTransportTaskHandler extends SimpleChannelInboundHandler<TaskTransportReqPacket> {

    public static final ServerTransportTaskHandler INSTANCE = new ServerTransportTaskHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TaskTransportReqPacket taskTransportReqPacket) {
        log.info("task id:" + taskTransportReqPacket.getTaskId());

        log.info("服务端发送消息");

        TaskTransportRespPacket respPacket = new TaskTransportRespPacket();
        respPacket.setResult("success");
        respPacket.setType(PacketType.TASK_TRANSPORT_RESP);

        ctx.channel().writeAndFlush(respPacket);
    }
}
