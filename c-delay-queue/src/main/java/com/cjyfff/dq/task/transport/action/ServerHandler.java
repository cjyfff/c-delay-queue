package com.cjyfff.dq.task.transport.action;

import java.nio.charset.Charset;
import java.util.Date;

import com.cjyfff.dq.task.transport.info.NodeChannelInfo;
import com.cjyfff.dq.task.transport.info.NodeChannelInfo.OneNodeChannelInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jiashen on 19-1-31.
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

        log.info(new Date() + ": 服务端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));

        Byte clientNodeId = Byte.valueOf(byteBuf.toString());
        if (! NodeChannelInfo.channelInfoMap.containsKey(clientNodeId)) {
            NodeChannelInfo.channelInfoMap.put(clientNodeId, new OneNodeChannelInfo(ctx.channel(), false));
        }

        byte[] bytes = "连接成功".getBytes(Charset.forName("utf-8"));

        ByteBuf buffer = ctx.alloc().buffer();

        buffer.writeBytes(bytes);
        ctx.channel().writeAndFlush(buffer);
    }
}