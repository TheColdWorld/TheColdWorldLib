package cn.thecoldworld.thecoldworldlib.mixins;

import net.minecraft.network.listener.ServerPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayPacketListener.class)
public interface ServerPlayPacketListenerInjector extends cn.thecoldworld.thecoldworldlib.networking.listeners.ServerPlayPacketListener {
}
