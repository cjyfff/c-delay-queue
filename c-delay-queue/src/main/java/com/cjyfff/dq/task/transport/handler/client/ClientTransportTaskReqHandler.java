package com.cjyfff.dq.task.transport.handler.client;

import com.cjyfff.dq.common.SpringUtils;
import com.cjyfff.dq.task.transport.biz.TransportAsyncBizService;
import com.cjyfff.dq.task.transport.protocol.PacketType;
import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
import com.cjyfff.dq.task.transport.protocol.TaskTransportRespPacket;
import com.cjyfff.election.core.info.ShardingInfo;
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

    private ClientTransportTaskReqHandler() {
        super();
        transportAsyncBizService = (TransportAsyncBizService) SpringUtils.getBean("transportAsyncBizService");
    }

    private TransportAsyncBizService transportAsyncBizService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TaskTransportReqPacket taskTransportReqPacket) {
        log.debug("ClientTransportTaskReqHandler get data，task id: {}", taskTransportReqPacket.getTaskId());

        transportAsyncBizService.asyncAcceptInnerMsg(taskTransportReqPacket);

        log.debug("ClientTransportTaskReqHandler send data");

        TaskTransportRespPacket respPacket = new TaskTransportRespPacket();
        respPacket.setTaskId(taskTransportReqPacket.getTaskId());
        respPacket.setResult("success");
        respPacket.setShardingId(ShardingInfo.getShardingId());
        respPacket.setType(PacketType.TASK_TRANSPORT_RESP);

        ctx.channel().writeAndFlush(respPacket);
    }
}
