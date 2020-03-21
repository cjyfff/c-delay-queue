package com.cjyfff.dq.task.transport.handler.server;

import com.cjyfff.dq.task.service.component.InnerMsgRecord;
import com.cjyfff.dq.task.transport.info.NodeChannelInfo;
import com.cjyfff.dq.task.transport.info.NodeChannelInfo.OneNodeChannelInfo;
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

    private ServerTransportTaskRespHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TaskTransportRespPacket respPacket) {

        InnerMsgRecord.innerMsgRecordMap.remove(respPacket.getTaskId());

        log.debug("ServerTransportTaskRespHandler get data -> {}", respPacket.getResult());
    }
}
