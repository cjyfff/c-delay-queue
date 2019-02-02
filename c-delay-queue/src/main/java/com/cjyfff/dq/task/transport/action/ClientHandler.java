package com.cjyfff.dq.task.transport.action;

import java.nio.charset.Charset;
import java.util.Date;

import com.cjyfff.dq.task.transport.protocol.PacketCoder;
import com.cjyfff.dq.task.transport.protocol.PacketType;
import com.cjyfff.dq.task.transport.protocol.TaskTransportReqPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jiashen on 19-1-31.
 */
@Slf4j
@Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {

    public static final ClientHandler INSTANCE = new ClientHandler();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info(new Date() + ": 客户端写出数据");

        TaskTransportReqPacket packet = new TaskTransportReqPacket();
        packet.setTaskId("asd1");
        packet.setType(PacketType.TASK_TRANSPORT);


        ByteBuf buffer = PacketCoder.INSTANT.encode(packet);

        ctx.channel().writeAndFlush(buffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

        log.info(new Date() + ": 客户端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));
    }
}
