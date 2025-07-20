package cn.thecoldworld.thecoldworldlib.networking.packet.c2s;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.exceptions.ServerBindPacketException;
import cn.thecoldworld.thecoldworldlib.networking.CommonPacketMetadata;
import cn.thecoldworld.thecoldworldlib.networking.packet.s2c.ExceptionS2CPacket;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CommonPlayC2SPacket<T> implements Packet<ServerPlayPacketListener> {
    public static final PacketType<CommonPlayC2SPacket<?>> TYPE = new PacketType<>(NetworkSide.SERVERBOUND, Identifier.of(Vars.MODID, "networking/c2s/common"));
    public static final PacketCodec<RegistryByteBuf, CommonPlayC2SPacket<?>> CODEC = PacketCodec.of(CommonPlayC2SPacket::write, CommonPlayC2SPacket::new);
    private static final Map<String, CommonPacketMetadata.ServerBind<?>> REGISTRIES = new LinkedHashMap<>();
    protected CommonPacketMetadata.ServerBind<T> metadata;
    protected NbtCompound data;

    public CommonPlayC2SPacket(CommonPacketMetadata.ServerBind<T> metadata, T data) {
        if (!REGISTRIES.containsKey(metadata.packetID.toString()))
            throw new IllegalArgumentException("metadata(ID='" + metadata.packetID.toString() + "' not registered");
        this.metadata = metadata;
        this.data = metadata.codec.encode(data, NbtOps.INSTANCE, new NbtCompound()).getOrThrow().asCompound().orElseThrow();
    }

    public CommonPlayC2SPacket(RegistryByteBuf buf) {
        this(buf.readNbt());
    }

    public CommonPlayC2SPacket(NbtCompound nbt) {
        metadata = (CommonPacketMetadata.ServerBind<T>) CommonPacketMetadata.ServerBind.CODEC.decode(NbtOps.INSTANCE, Objects.requireNonNull(Objects.requireNonNull(nbt).get("metadata")).asCompound().orElseThrow()).getOrThrow().getFirst();
        data = Objects.requireNonNull(nbt.get("data")).asCompound().orElseThrow();
    }

    @Contract("_ -> param1")
    public static <T> CommonPacketMetadata.@NotNull ServerBind<T> register(CommonPacketMetadata.ServerBind<T> metadata) {
        REGISTRIES.put(metadata.packetID.toString(), metadata);
        return metadata;
    }

    public CommonPacketMetadata.ServerBind<T> getMetadata() {
        return metadata;
    }

    public NbtCompound getData() {
        return data;
    }

    private void write(RegistryByteBuf buf) {
        buf.writeNbt(asNBT());
    }

    @Override
    public PacketType<? extends Packet<ServerPlayPacketListener>> getPacketType() {
        return TYPE;
    }

    @Override
    public void apply(ServerPlayPacketListener listener) {
        try {
            cn.thecoldworld.thecoldworldlib.networking.listeners.ServerPlayPacketListener.of(listener).TheColdWorldLib$common(REGISTRIES.get(metadata.packetID.toString()), data);
        } catch (ServerBindPacketException e) {
            e.connection.send(new ExceptionS2CPacket(this, e.getCause()));
        }
    }

    public NbtCompound asNBT(final @NotNull NbtCompound input) {
        input.put("metadata", CommonPacketMetadata.ServerBind.CODEC.encode(metadata, NbtOps.INSTANCE, new NbtCompound()).getOrThrow());
        input.put("data", data);
        return input;
    }

    public NbtCompound asNBT() {
        return asNBT(new NbtCompound());
    }
}
