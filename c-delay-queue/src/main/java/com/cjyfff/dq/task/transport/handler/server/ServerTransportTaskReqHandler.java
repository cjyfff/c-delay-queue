package com.cjyfff.dq.task.transport.handler.server;

import com.cjyfff.dq.task.transport.info.NodeChannelInfo;
import com.cjyfff.dq.task.transport.info.NodeChannelInfo.OneNodeChannelInfo;
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
public class ServerTransportTaskReqHandler extends SimpleChannelInboundHandler<TaskTransportReqPacket> {

    public static final ServerTransportTaskReqHandler INSTANCE = new ServerTransportTaskReqHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TaskTransportReqPacket taskTransportReqPacket) {
        log.info("服务端req handler收到消息，task id:" + taskTransportReqPacket.getTaskId());

        log.info("服务端req handler发送消息");

        NodeChannelInfo.channelInfoMap.put(taskTransportReqPacket.getNodeId(),
            new OneNodeChannelInfo(ctx.channel(), true));


        TaskTransportRespPacket respPacket = new TaskTransportRespPacket();
        respPacket.setTaskId(taskTransportReqPacket.getTaskId());
        respPacket.setResult("success");
        respPacket.setType(PacketType.TASK_TRANSPORT_RESP);

        ctx.channel().writeAndFlush(respPacket);
    }
}
