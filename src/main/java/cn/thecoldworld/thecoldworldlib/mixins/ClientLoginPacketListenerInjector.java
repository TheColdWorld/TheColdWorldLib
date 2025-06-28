package cn.thecoldworld.thecoldworldlib.mixins;


import net.minecraft.network.listener.ClientLoginPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientLoginPacketListener.class)
public interface ClientLoginPacketListenerInjector extends cn.thecoldworld.thecoldworldlib.networking.listeners.ClientLoginPacketListener {
}
