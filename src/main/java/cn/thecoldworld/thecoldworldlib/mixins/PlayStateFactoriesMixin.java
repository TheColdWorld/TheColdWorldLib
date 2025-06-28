package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.events.Events;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.state.NetworkStateBuilder;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayStateFactories.class)
public class PlayStateFactoriesMixin {
    @Inject(method = "method_55959", at = @At("RETURN"))
    private static void c2s(NetworkStateBuilder<ServerPlayPacketListener, RegistryByteBuf, PlayStateFactories.PacketCodecModifierContext> builder, CallbackInfo ci) {
        Events.SERVERSIDE_PLAY_PACKAGE_ONREGISTER.Invoke(builder);
    }

    @Inject(method = "method_55958", at = @At("RETURN"))
    private static void s2c(NetworkStateBuilder<ClientPlayPacketListener, RegistryByteBuf, Unit> builder, CallbackInfo ci) {
        Events.CLIENTSIDE_PLAY_PACKAGE_ONREGISTER.Invoke(builder);
    }
}
