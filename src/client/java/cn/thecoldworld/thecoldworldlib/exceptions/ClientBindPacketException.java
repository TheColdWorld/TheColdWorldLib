package cn.thecoldworld.thecoldworldlib.exceptions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;


public class ClientBindPacketException extends ClientBindPacketExceptionBase {
    public final MinecraftClient client;

    public ClientBindPacketException(MinecraftClient client, World clientWorld, ClientConnection connection, Random random, Throwable cause) {
        super(clientWorld, connection, random, cause);
        this.client = client;
    }
}
