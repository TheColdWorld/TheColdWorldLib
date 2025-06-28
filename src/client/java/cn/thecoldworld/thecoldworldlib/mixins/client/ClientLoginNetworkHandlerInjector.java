package cn.thecoldworld.thecoldworldlib.mixins.client;


import cn.thecoldworld.thecoldworldlib.networking.listeners.ClientLoginPacketListener;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientLoginNetworkHandler.class)
public interface ClientLoginNetworkHandlerInjector extends ClientLoginPacketListener {
}
