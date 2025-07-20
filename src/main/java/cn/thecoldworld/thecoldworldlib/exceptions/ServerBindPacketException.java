package cn.thecoldworld.thecoldworldlib.exceptions;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerBindPacketException extends Exception {
    public final MinecraftServer server;
    public final ServerPlayerEntity player;
    public final ClientConnection connection;

    public ServerBindPacketException(MinecraftServer server, ServerPlayerEntity player, ClientConnection connection, Throwable cause) {
        super(cause);
        this.server = server;
        this.player = player;
        this.connection = connection;
    }
}
