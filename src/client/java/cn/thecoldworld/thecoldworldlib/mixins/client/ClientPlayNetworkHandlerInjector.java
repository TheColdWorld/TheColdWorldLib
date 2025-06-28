package cn.thecoldworld.thecoldworldlib.mixins.client;

import cn.thecoldworld.thecoldworldlib.networking.listeners.ClientPlayPacketListener;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerInjector extends ClientPlayPacketListener {
}
