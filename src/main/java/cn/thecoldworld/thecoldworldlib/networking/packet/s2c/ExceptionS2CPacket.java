package cn.thecoldworld.thecoldworldlib.networking.packet.s2c;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.exceptions.SerializedException;
import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.CommonPlayC2SPacket;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;

import java.time.Instant;
import java.util.Objects;

public class ExceptionS2CPacket implements Packet<ClientPlayPacketListener> {
    public static final PacketType<ExceptionS2CPacket> TYPE = new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.of(Vars.MODID, "networking/c2s/play/exception"));
    public static final PacketCodec<RegistryByteBuf, ExceptionS2CPacket> CODEC = PacketCodec.of(ExceptionS2CPacket::write, ExceptionS2CPacket::new);
    protected final CommonPlayC2SPacket<?> exceptionPacket;
    protected final SerializedException exception;
    protected final long time;

    public ExceptionS2CPacket(CommonPlayC2SPacket<?> exceptionPacket, Throwable cause) {
        time = Instant.now().getEpochSecond();
        this.exceptionPacket = exceptionPacket;
        this.exception = new SerializedException(cause);
    }

    protected ExceptionS2CPacket(RegistryByteBuf buf) {
        NbtCompound nbt = Objects.requireNonNull(buf.readNbt()).asCompound().orElseThrow();
        exceptionPacket = new CommonPlayC2SPacket<>(Objects.requireNonNull(nbt.get("packet")).asCompound().orElseThrow());
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
    public PacketType<? extends Packet<ClientPlayPacketListener>> getPacketType() {
        return TYPE;
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        cn.thecoldworld.thecoldworldlib.networking.listeners.ClientPlayPacketListener.of(listener).TheColdWorldLib$exception(exceptionPacket, exception, time);
    }
}
