package cn.thecoldworld.thecoldworldlib.networking.listeners;

import cn.thecoldworld.thecoldworldlib.exceptions.SerializedException;
import cn.thecoldworld.thecoldworldlib.exceptions.ServerBindPacketException;
import cn.thecoldworld.thecoldworldlib.networking.CommonPacketMetadata;
import cn.thecoldworld.thecoldworldlib.networking.packet.s2c.CommonPlayS2CPacket;
import net.minecraft.nbt.NbtCompound;

public interface ServerPlayPacketListener {
    static ServerPlayPacketListener of(net.minecraft.network.listener.ServerPlayPacketListener listener) {
        return ((cn.thecoldworld.thecoldworldlib.networking.listeners.ServerPlayPacketListener) listener);
    }

    default <T> void TheColdWorldLib$common(CommonPacketMetadata.ServerBind<T> metadata, NbtCompound content) throws ServerBindPacketException {
        throw new AssertionError();
    }

    default <T> void TheColdWorldLib$exception(final CommonPlayS2CPacket<T> exceptionPacket, final SerializedException exception, long time) {
        throw new AssertionError();
    }
}
