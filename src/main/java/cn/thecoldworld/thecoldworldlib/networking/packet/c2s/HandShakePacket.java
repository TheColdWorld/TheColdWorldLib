package cn.thecoldworld.thecoldworldlib.networking.packet.c2s;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.submod.Mod;
import cn.thecoldworld.thecoldworldlib.submod.ModManager;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.Optional;

public class HandShakePacket implements Packet<ServerLoginPacketListener> {
    public static final PacketType<HandShakePacket> TYPE = new PacketType<>(NetworkSide.SERVERBOUND, Identifier.of(Vars.MODID, "networking/c2s/login/handshake"));
    public static final PacketCodec<PacketByteBuf, HandShakePacket> CODEC = Packet.createCodec(HandShakePacket::write, HandShakePacket::new);
    public final String RequiredModVersionString;
    public final String VersionString;
    public final Version Version;
    public final VersionPredicate RequiredModVersion;
    public final ModManager manager;

    public HandShakePacket() {
        this.RequiredModVersionString = Vars.COMPATIBLE_VERSION;
        this.VersionString = Vars.MOD_VERSION;
        manager = ModManager.getInstance();
        try {
            this.Version = net.fabricmc.loader.api.Version.parse(this.VersionString);
            this.RequiredModVersion = VersionPredicate.parse(this.RequiredModVersionString);
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    protected HandShakePacket(PacketByteBuf buf) {
        NbtCompound nbt = buf.readNbt();
        if (Objects.isNull(nbt)) throw new IllegalArgumentException("packet invaild:not a nbt object");
        this.RequiredModVersionString = nbt.getString("RequiredVersions").orElseThrow();
        this.VersionString = nbt.getString("Version").orElseThrow();
        var modlist = Objects.requireNonNull(nbt.get("Mods")).asNbtList().orElseThrow();
        manager = new ModManager(modlist.size());
        for (NbtElement n : modlist) {
            Optional<NbtCompound> modnbt = n.asCompound();
            modnbt.ifPresent(nbtCompound -> manager.register(Mod.CODEC.decode(NbtOps.INSTANCE, nbtCompound).getOrThrow().getFirst()));
        }

        manager.freeze();
        try {
            this.Version = net.fabricmc.loader.api.Version.parse(this.VersionString);
            this.RequiredModVersion = VersionPredicate.parse(this.RequiredModVersionString);
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(PacketByteBuf buf) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("RequiredVersions", this.RequiredModVersionString);
        nbt.putString("Version", this.VersionString);
        NbtList list = new NbtList();
        for (Mod mod : manager) {
            list.add(Mod.CODEC.encode(mod, NbtOps.INSTANCE, null).getOrThrow());
        }
        nbt.put("Mods", list);
        buf.writeNbt(nbt);
    }

    @Override
    public PacketType<? extends Packet<ServerLoginPacketListener>> getPacketType() {
        return TYPE;
    }

    @Override
    public void apply(ServerLoginPacketListener listener) {
        ((cn.thecoldworld.thecoldworldlib.networking.listeners.ServerLoginPacketListener) listener).TheColdWorldLib$OnHandShake(this);
    }

    public boolean isCompatible(ModMetadata metadata) {
        return Vars.MODID.equals(metadata.getId()) && isCompatible(metadata.getVersion());
    }

    public boolean isCompatible(ModContainer mod) {
        return isCompatible(mod.getMetadata());
    }

    public boolean isCompatible(Version Version) {
        return RequiredModVersion.test(Version);
    }

    public boolean isCompatible(String Version) {
        try {
            return this.isCompatible(net.fabricmc.loader.api.Version.parse(Version));
        } catch (VersionParsingException e) {
            return false;
        }
    }
}
