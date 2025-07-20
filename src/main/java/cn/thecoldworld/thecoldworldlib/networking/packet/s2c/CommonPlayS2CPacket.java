package cn.thecoldworld.thecoldworldlib.networking.packet.s2c;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.exceptions.ClientBindPacketExceptionBase;
import cn.thecoldworld.thecoldworldlib.networking.CommonPacketMetadata;
import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.ExceptionC2SPacket;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CommonPlayS2CPacket<T> implements Packet<ClientPlayPacketListener> {
    public static final PacketType<CommonPlayS2CPacket<?>> TYPE = new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.of(Vars.MODID, "networking/s2c/common"));
    public static final PacketCodec<RegistryByteBuf, CommonPlayS2CPacket<?>> CODEC = PacketCodec.of(CommonPlayS2CPacket::write, CommonPlayS2CPacket::new);
    private static final Map<String, CommonPacketMetadata.ClientBind<?>> REGISTRIES = new LinkedHashMap<>();
    protected CommonPacketMetadata.ClientBind<T> metadata;
    protected NbtCompound data;

    public CommonPlayS2CPacket(CommonPacketMetadata.ClientBind<T> metadata, T data) {
        if (!REGISTRIES.containsKey(metadata.packetID.toString()))
            throw new IllegalArgumentException("metadata(ID='" + metadata.packetID + "' not registered");
        this.metadata = metadata;
        this.data = metadata.codec.encode(data, NbtOps.INSTANCE, new NbtCompound()).getOrThrow().asCompound().orElseThrow();
    }

    public CommonPlayS2CPacket(RegistryByteBuf buf) {
        this(buf.readNbt());
    }

    public CommonPlayS2CPacket(NbtCompound nbt) {
        metadata = (CommonPacketMetadata.ClientBind<T>) CommonPacketMetadata.ClientBind.CODEC.decode(NbtOps.INSTANCE, Objects.requireNonNull(Objects.requireNonNull(nbt).get("metadata")).asCompound().orElseThrow()).getOrThrow().getFirst();
        data = Objects.requireNonNull(nbt.get("data")).asCompound().orElseThrow();
    }

    @Contract("_ -> param1")
    public static <T> CommonPacketMetadata.@NotNull ClientBind<T> register(CommonPacketMetadata.ClientBind<T> metadata) {
        REGISTRIES.put(metadata.packetID.toString(), metadata);
        return metadata;
    }

    public CommonPacketMetadata.ClientBind<T> getMetadata() {
        return metadata;
    }

    public NbtCompound getData() {
        return data;
    }

    @Override
    public PacketType<? extends Packet<ClientPlayPacketListener>> getPacketType() {
        return TYPE;
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        try {
            cn.thecoldworld.thecoldworldlib.networking.listeners.ClientPlayPacketListener.of(listener).TheColdWorldLib$common(REGISTRIES.get(metadata.packetID.toString()), data);
        } catch (ClientBindPacketExceptionBase e) {
            e.connection.send(new ExceptionC2SPacket(this, e.getCause()));
        }
    }

    public void write(RegistryByteBuf buf) {
        buf.writeNbt(asNBT());
    }

    public NbtCompound asNBT(@NotNull NbtCompound input) {
        input.put("metadata", CommonPacketMetadata.ClientBind.CODEC.encode(metadata, NbtOps.INSTANCE, new NbtCompound()).getOrThrow());
        input.put("data", data);
        return input;
    }

    public NbtCompound asNBT() {
        return asNBT(new NbtCompound());
    }
}
