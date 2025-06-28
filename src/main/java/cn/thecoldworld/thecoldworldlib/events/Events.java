package cn.thecoldworld.thecoldworldlib.events;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.state.NetworkStateBuilder;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.util.Unit;

public final class Events {
    public static final OneShotEvent<NetworkStateBuilder<ClientLoginPacketListener, PacketByteBuf, Unit>> CLIENTSIDE_LOGIN_PACKAGE_ONREGISTER = new OneShotEvent<>();
    public static final OneShotEvent<NetworkStateBuilder<ServerLoginPacketListener, PacketByteBuf, Unit>> SERVERSIDE_LOGIN_PACKAGE_ONREGISTER = new OneShotEvent<>();
    public static final OneShotEvent<NetworkStateBuilder<ClientPlayPacketListener, RegistryByteBuf, Unit>> CLIENTSIDE_PLAY_PACKAGE_ONREGISTER = new OneShotEvent<NetworkStateBuilder<ClientPlayPacketListener, RegistryByteBuf, Unit>>();
    public static final OneShotEvent<NetworkStateBuilder<ServerPlayPacketListener, RegistryByteBuf, PlayStateFactories.PacketCodecModifierContext>> SERVERSIDE_PLAY_PACKAGE_ONREGISTER = new OneShotEvent<NetworkStateBuilder<ServerPlayPacketListener, RegistryByteBuf, net.minecraft.network.state.PlayStateFactories.PacketCodecModifierContext>>();

    private Events() {
    }
}
