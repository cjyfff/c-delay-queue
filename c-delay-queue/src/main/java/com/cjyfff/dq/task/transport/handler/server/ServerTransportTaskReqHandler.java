package com.cjyfff.dq.task.transport.handler.server;

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
 * Created by jiashen on 19-2-2.
 */
@Slf4j
@Sharable
public class ServerTransportTaskReqHandler extends SimpleChannelInboundHandler<TaskTransportReqPacket> {

    public static final ServerTransportTaskReqHandler INSTANCE = new ServerTransportTaskReqHandler();

    public ServerTransportTaskReqHandler() {
        super();
        transportAsyncBizService = (TransportAsyncBizService) SpringUtils.getBean("transportAsyncBizService");
    }

    private TransportAsyncBizService transportAsyncBizService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TaskTransportReqPacket taskTransportReqPacket) {
        log.debug("ServerTransportTaskReqHandler get msg，task id:{}", taskTransportReqPacket.getTaskId());

        transportAsyncBizService.asyncAcceptInnerMsg(taskTransportReqPacket);

        log.debug("ServerTransportTaskReqHandler send Msg");

        TaskTransportRespPacket respPacket = new TaskTransportRespPacket();
        respPacket.setShardingId(ShardingInfo.getShardingId());
        respPacket.setTaskId(taskTransportReqPacket.getTaskId());
        respPacket.setResult("success");
        respPacket.setType(PacketType.TASK_TRANSPORT_RESP);

        ctx.channel().writeAndFlush(respPacket);
    }
}
