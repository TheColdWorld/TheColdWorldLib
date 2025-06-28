package cn.thecoldworld.thecoldworldlib.networking.listeners;

import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.HandShakePacket;

public interface ServerLoginPacketListener {
    default void TheColdWorldLib$OnHandShake(HandShakePacket packet) {
        throw new AssertionError();
    }
}
