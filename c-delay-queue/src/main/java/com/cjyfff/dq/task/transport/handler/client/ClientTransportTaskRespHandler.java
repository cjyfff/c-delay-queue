package com.cjyfff.dq.task.transport.handler.client;

import com.cjyfff.dq.task.service.component.InnerMsgRecord;
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
public class ClientTransportTaskRespHandler extends SimpleChannelInboundHandler<TaskTransportRespPacket> {

    public static final ClientTransportTaskRespHandler INSTANCE = new ClientTransportTaskRespHandler();

    private ClientTransportTaskRespHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TaskTransportRespPacket respPacket) {

        InnerMsgRecord.innerMsgRecordMap.remove(respPacket.getTaskId());

        log.debug("ClientTransportTaskRespHandler get data -> {}", respPacket.getResult());
    }
}
