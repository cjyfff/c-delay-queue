package com.cjyfff.dq.task.transport.handler;

import java.util.List;

import com.cjyfff.dq.task.transport.protocol.PacketCoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Created by jiashen on 19-2-2.
 */
@Sharable
public class PacketDecoder extends ByteToMessageDecoder {

    public static final PacketDecoder INSTANCE = new PacketDecoder();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) {
        out.add(PacketCoder.INSTANCE.decode(in));
    }
}
