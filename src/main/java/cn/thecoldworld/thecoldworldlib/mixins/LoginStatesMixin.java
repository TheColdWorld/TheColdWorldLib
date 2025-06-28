package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.events.Events;
import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.HandShakePacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.state.LoginStates;
import net.minecraft.network.state.NetworkStateBuilder;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoginStates.class)
public class LoginStatesMixin {
    @Inject(method = "method_56018", at = @At("RETURN"))
    private static void s2c(NetworkStateBuilder<ClientLoginPacketListener, PacketByteBuf, Unit> builder, CallbackInfo ci) {
        Events.CLIENTSIDE_LOGIN_PACKAGE_ONREGISTER.Invoke(builder);
    }

    @Inject(method = "method_56019", at = @At("RETURN"))
    private static void c2s(NetworkStateBuilder<ServerLoginPacketListener, PacketByteBuf, Unit> builder, CallbackInfo ci) {
        builder.add(HandShakePacket.TYPE, HandShakePacket.CODEC);
        Events.SERVERSIDE_LOGIN_PACKAGE_ONREGISTER.Invoke(builder);
    }
}
