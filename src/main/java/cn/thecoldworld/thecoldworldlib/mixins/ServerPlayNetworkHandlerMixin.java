package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.networking.listeners.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListener {
}
