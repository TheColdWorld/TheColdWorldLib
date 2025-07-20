package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.interfaces.mixin.IGetClientConnection;
import cn.thecoldworld.thecoldworldlib.interfaces.mixin.IGetMinecraftServer;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerCommonNetworkHandler.class)
public class ServerCommonNetworkHandlerMixin implements IGetMinecraftServer, IGetClientConnection {
    @Shadow
    @Final
    protected ClientConnection connection;

    @Shadow
    @Final
    protected MinecraftServer server;

    @Override
    public ClientConnection getConnection() {
        return connection;
    }

    @Override
    public MinecraftServer getServer() {
        return server;
    }
}
