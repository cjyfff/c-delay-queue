package com.cjyfff.dq.task.transport.protocol;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.cjyfff.dq.task.transport.conf.BaseTransportConf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * Created by jiashen on 2019/2/2.
 */
public class PacketCoder {

    public static final PacketCoder INSTANCE = new PacketCoder();

    private final static Map<Byte, Class<? extends Packet>> PACKET_TYPE_CLASS_MAP =
        new HashMap<Byte, Class<? extends Packet>>() {
            {
                put(PacketType.TASK_TRANSPORT_REQ, TaskTransportReqPacket.class);
                put(PacketType.TASK_TRANSPORT_RESP, TaskTransportRespPacket.class);
            }
        };

    public ByteBuf encode(Packet packet) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        byte[] bytes = JSON.toJSONBytes(packet);

        byteBuf.writeInt(BaseTransportConf.P_ID_NUMBER);
        byteBuf.writeByte(BaseTransportConf.DEFAULT_PACKET_VERSION);
        byteBuf.writeByte(packet.getType());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }

    public ByteBuf encode(ByteBuf byteBuf, Packet packet) {

        byte[] bytes = JSON.toJSONBytes(packet);

        byteBuf.writeInt(BaseTransportConf.P_ID_NUMBER);
        byteBuf.writeByte(BaseTransportConf.DEFAULT_PACKET_VERSION);
        byteBuf.writeByte(packet.getType());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }

    public Packet decode(ByteBuf byteBuf) {
        byteBuf.skipBytes(4);

        byteBuf.skipBytes(1);

        byte type = byteBuf.readByte();

        int length = byteBuf.readInt();

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> requestType = PACKET_TYPE_CLASS_MAP.get(type);

        if (requestType != null) {
            return JSON.parseObject(bytes, requestType);
        }

        return null;
    }
}
