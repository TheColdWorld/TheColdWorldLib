package cn.thecoldworld.thecoldworldlib.mixins;

import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnterConfigurationC2SPacket.class)
public class EnterConfigurationC2SPacketMixin {
    @Inject(method = "apply(Lnet/minecraft/network/listener/ServerLoginPacketListener;)V", at = @At("RETURN"))
    public void afterConfiguration(ServerLoginPacketListener serverLoginPacketListener, CallbackInfo ci) {

    }
}
