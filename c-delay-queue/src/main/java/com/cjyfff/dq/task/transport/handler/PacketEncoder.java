package com.cjyfff.dq.task.transport.handler;

import com.cjyfff.dq.task.transport.protocol.Packet;
import com.cjyfff.dq.task.transport.protocol.PacketCoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by jiashen on 19-2-2.
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {


    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        PacketCoder.INSTANCE.encode(out, packet);
    }
}
