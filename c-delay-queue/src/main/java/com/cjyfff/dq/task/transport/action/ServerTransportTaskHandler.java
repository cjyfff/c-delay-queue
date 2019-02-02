package com.cjyfff.dq.task.transport.action;

import java.nio.charset.Charset;

import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jiashen on 19-2-2.
 */
@Slf4j
public class ServerTransportTaskHandler extends SimpleChannelInboundHandler<TaskTransportReqPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TaskTransportReqPacket taskTransportReqPacket) {
        log.info("task id:" + taskTransportReqPacket.getTaskId());

        byte[] bytes = "连接成功".getBytes(Charset.forName("utf-8"));

        ByteBuf buffer = ctx.alloc().buffer();

        buffer.writeBytes(bytes);

        log.info("服务端发送消息: " + new String(bytes));

        ctx.channel().writeAndFlush(buffer);
    }
}
