package cn.thecoldworld.thecoldworldlib.interfaces.mixin;

import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.HandShakePacket;

public interface IHandshakePacketAccessor {
    HandShakePacket getHandshakePacket();

    void setHandshakePacket(HandShakePacket handShakePacket);
}
