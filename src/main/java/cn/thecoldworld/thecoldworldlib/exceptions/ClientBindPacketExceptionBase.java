package cn.thecoldworld.thecoldworldlib.exceptions;

import net.minecraft.network.ClientConnection;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;


public class ClientBindPacketExceptionBase extends Exception {
    public final World clientWorld;
    public final ClientConnection connection;
    public final Random random;

    public ClientBindPacketExceptionBase(World clientWorld, ClientConnection connection, Random random, Throwable cause) {
        super(cause);
        this.clientWorld = clientWorld;
        this.connection = connection;
        this.random = random;
    }
}
