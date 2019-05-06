package com.cjyfff.dq.task.transport.info;

import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Created by jiashen on 19-2-27.
 */
public class EventLoopGroupInfo {
    public static NioEventLoopGroup clientNioEventLoopGroup;

    public static NioEventLoopGroup serverBoosGroup;

    public static NioEventLoopGroup serverWorkerGroup;
}
