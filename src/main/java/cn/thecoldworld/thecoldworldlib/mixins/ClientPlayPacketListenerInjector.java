package cn.thecoldworld.thecoldworldlib.mixins;

import net.minecraft.network.listener.ClientPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayPacketListener.class)
public interface ClientPlayPacketListenerInjector extends cn.thecoldworld.thecoldworldlib.networking.listeners.ClientPlayPacketListener {
}
