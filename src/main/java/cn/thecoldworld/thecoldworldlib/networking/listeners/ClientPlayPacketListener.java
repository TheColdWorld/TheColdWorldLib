package cn.thecoldworld.thecoldworldlib.networking.listeners;


import cn.thecoldworld.thecoldworldlib.exceptions.ClientBindPacketExceptionBase;
import cn.thecoldworld.thecoldworldlib.exceptions.SerializedException;
import cn.thecoldworld.thecoldworldlib.networking.CommonPacketMetadata;
import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.CommonPlayC2SPacket;
import net.minecraft.nbt.NbtCompound;

public interface ClientPlayPacketListener {
    static ClientPlayPacketListener of(net.minecraft.network.listener.ClientPlayPacketListener listener) {
        return (ClientPlayPacketListener) listener;
    }

    default <T> void TheColdWorldLib$common(CommonPacketMetadata.ClientBind<T> metadata, NbtCompound content) throws ClientBindPacketExceptionBase {
        throw new AssertionError();
    }

    default <T> void TheColdWorldLib$exception(final CommonPlayC2SPacket<T> exceptionPacket, final SerializedException exception, long time) {
        throw new AssertionError();
    }
}
