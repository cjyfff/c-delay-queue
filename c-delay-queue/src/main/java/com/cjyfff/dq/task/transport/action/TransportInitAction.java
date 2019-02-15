package com.cjyfff.dq.task.transport.action;

import com.cjyfff.dq.task.transport.handler.PacketDecoder;
import com.cjyfff.dq.task.transport.handler.PacketEncoder;
import com.cjyfff.dq.task.transport.handler.Spliter;
import com.cjyfff.dq.task.transport.handler.server.ServerGetClientInitInfoHandler;
import com.cjyfff.dq.task.transport.handler.server.ServerTransportTaskReqHandler;
import com.cjyfff.dq.task.transport.handler.server.ServerTransportTaskRespHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 19-2-14.
 */
@Component
@Slf4j
public class TransportInitAction {

    @Value("${l_election.specified_port}")
    private int transportPort;

    public void startServer() {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
            .group(boosGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) {
                    ch.pipeline()
                        .addLast(new Spliter())
                        .addLast(new PacketDecoder())
                        .addLast(ServerGetClientInitInfoHandler.INSTANCE)
                        .addLast(ServerTransportTaskReqHandler.INSTANCE)
                        .addLast(ServerTransportTaskRespHandler.INSTANCE)
                        .addLast(new PacketEncoder());
                }
            });

        serverBootstrap.bind(transportPort).addListener(future -> {
            if (future.isSuccess()) {
                log.info("Success to create netty server on {} port", transportPort);
            } else {
                log.info("Fail to create netty server on {} port", transportPort);
            }
        });
    }
}
