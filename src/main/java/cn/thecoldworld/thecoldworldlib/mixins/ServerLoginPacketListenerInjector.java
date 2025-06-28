package cn.thecoldworld.thecoldworldlib.mixins;

import net.minecraft.network.listener.ServerLoginPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLoginPacketListener.class)
public interface ServerLoginPacketListenerInjector extends cn.thecoldworld.thecoldworldlib.networking.listeners.ServerLoginPacketListener {
}
