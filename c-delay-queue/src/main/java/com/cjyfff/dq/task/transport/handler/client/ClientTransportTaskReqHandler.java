package com.cjyfff.dq.task.transport.handler.client;

import com.cjyfff.dq.task.transport.protocol.PacketType;
import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
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
public class ClientTransportTaskReqHandler extends SimpleChannelInboundHandler<TaskTransportReqPacket> {
    public static final ClientTransportTaskReqHandler INSTANCE = new ClientTransportTaskReqHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TaskTransportReqPacket taskTransportReqPacket) {
        log.info("客户端req handler收到消息，task id:" + taskTransportReqPacket.getTaskId());

        log.info("客户端req handler发送消息");

        TaskTransportRespPacket respPacket = new TaskTransportRespPacket();
        respPacket.setTaskId(taskTransportReqPacket.getTaskId());
        respPacket.setResult("success");
        respPacket.setType(PacketType.TASK_TRANSPORT_RESP);

        ctx.channel().writeAndFlush(respPacket);
    }
}
