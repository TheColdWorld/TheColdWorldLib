package cn.thecoldworld.thecoldworldlib.mixins.client;

import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.HandShakePacket;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "startIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.BEFORE))
    private void onConnectLocalServer(LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, boolean newWorld, CallbackInfo ci, @Local ClientConnection clientConnection) {
        clientConnection.send(new HandShakePacket());
    }
}
