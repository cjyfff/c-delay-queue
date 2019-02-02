package com.cjyfff.dq.task.transport.action;

import java.nio.charset.Charset;
import java.util.Date;

import com.cjyfff.dq.task.transport.info.NodeChannelInfo;
import com.cjyfff.dq.task.transport.info.NodeChannelInfo.OneNodeChannelInfo;
import com.cjyfff.dq.task.transport.protocol.Packet;
import com.cjyfff.dq.task.transport.protocol.PacketCoder;
import com.cjyfff.dq.task.transport.protocol.TaskTransportPacket;
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
public class ServerHandler extends ChannelInboundHandlerAdapter {

    public static final ServerHandler INSTANCE = new ServerHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

        log.info(new Date() + ": 服务端读到数据");

        Packet packet = PacketCoder.INSTANT.decode(byteBuf);

        if (packet == null) {
            return;
        }

        Byte clientNodeId = packet.getNodeId();
        if (! NodeChannelInfo.channelInfoMap.containsKey(clientNodeId)) {
            NodeChannelInfo.channelInfoMap.put(clientNodeId, new OneNodeChannelInfo(ctx.channel(), false));
        }

        log.info("task id:" + ((TaskTransportPacket)packet).getTaskId());

        byte[] bytes = "连接成功".getBytes(Charset.forName("utf-8"));

        ByteBuf buffer = ctx.alloc().buffer();

        buffer.writeBytes(bytes);

        log.info("服务端发送消息: " + new String(bytes));

        ctx.channel().writeAndFlush(buffer);
    }
}
