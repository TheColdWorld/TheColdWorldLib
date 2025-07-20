package cn.thecoldworld.thecoldworldlib.networking.packet.c2s;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.exceptions.SerializedException;
import cn.thecoldworld.thecoldworldlib.networking.packet.s2c.CommonPlayS2CPacket;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;

import java.time.Instant;
import java.util.Objects;

public class ExceptionC2SPacket implements Packet<ServerPlayPacketListener> {
    public static final PacketType<ExceptionC2SPacket> TYPE = new PacketType<>(NetworkSide.SERVERBOUND, Identifier.of(Vars.MODID, "networking/c2s/play/exception"));
    public static final PacketCodec<RegistryByteBuf, ExceptionC2SPacket> CODEC = PacketCodec.of(ExceptionC2SPacket::write, ExceptionC2SPacket::new);
    protected final CommonPlayS2CPacket<?> exceptionPacket;
    protected final SerializedException exception;
    protected final long time;

    public <T> ExceptionC2SPacket(CommonPlayS2CPacket<T> exceptionPacket, Throwable cause) {
        time = Instant.now().getEpochSecond();
        this.exceptionPacket = exceptionPacket;
        this.exception = new SerializedException(cause);
    }

    protected ExceptionC2SPacket(RegistryByteBuf buf) {
        NbtCompound nbt = Objects.requireNonNull(buf.readNbt()).asCompound().orElseThrow();
        exceptionPacket = new CommonPlayS2CPacket<>(Objects.requireNonNull(nbt.get("packet")).asCompound().orElseThrow());
        exception = SerializedException.CODEC.decode(NbtOps.INSTANCE, Objects.requireNonNull(nbt.get("exception")).asCompound().orElseThrow()).getOrThrow().getFirst();
        time = nbt.getLong("occortime").orElseThrow();
    }


    public void write(RegistryByteBuf buf) {
        NbtCompound nbt = new NbtCompound();
        nbt.put("packet", exceptionPacket.asNBT());
        nbt.put("exception", SerializedException.CODEC.encode(exception, NbtOps.INSTANCE, new NbtCompound()).getOrThrow());
        nbt.putLong("occortime", time);
        buf.writeNbt(nbt);
    }

    @Override
    public PacketType<? extends Packet<ServerPlayPacketListener>> getPacketType() {
        return TYPE;
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        cn.thecoldworld.thecoldworldlib.networking.listeners.ServerPlayPacketListener.of(listener).TheColdWorldLib$exception(exceptionPacket, exception, time);
    }

}
