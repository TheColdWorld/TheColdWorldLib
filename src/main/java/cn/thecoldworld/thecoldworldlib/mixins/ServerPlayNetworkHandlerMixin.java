package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.exceptions.SerializedException;
import cn.thecoldworld.thecoldworldlib.exceptions.ServerBindPacketException;
import cn.thecoldworld.thecoldworldlib.interfaces.mixin.IGetClientConnection;
import cn.thecoldworld.thecoldworldlib.interfaces.mixin.IGetMinecraftServer;
import cn.thecoldworld.thecoldworldlib.networking.CommonPacketMetadata;
import cn.thecoldworld.thecoldworldlib.networking.listeners.ServerPlayPacketListener;
import cn.thecoldworld.thecoldworldlib.networking.packet.s2c.CommonPlayS2CPacket;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.time.Instant;
import java.time.ZoneId;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListener {
    @Shadow
    @Final
    static Logger LOGGER;
    @Shadow
    public ServerPlayerEntity player;

    @Override
    public <T> void TheColdWorldLib$common(CommonPacketMetadata.ServerBind<T> metadata, NbtCompound content) throws ServerBindPacketException {
        ServerCommonNetworkHandler handler = (ServerCommonNetworkHandler) (Object) this;
        MinecraftServer server = ((IGetMinecraftServer) handler).getServer();
        ClientConnection connection = ((IGetClientConnection) handler).getConnection();
        try {
            metadata.callback.onReply(server, player, connection, LOGGER, metadata.codec.decode(NbtOps.INSTANCE, content).getOrThrow().getFirst());
        } catch (IllegalCallerException e) {
            throw new CrashException(new CrashReport("internal exception:Illegal caller from packet" + metadata.packetID.toString(), e));
        } catch (Throwable throwable) {
            throw new ServerBindPacketException(server, player, connection, throwable);
        }
    }

    @Override
    public <T> void TheColdWorldLib$exception(CommonPlayS2CPacket<T> exceptionPacket, SerializedException exception, long time) {
        LOGGER.error("Client occored exception {} at {} in package{},message{}", exception.className, Instant.ofEpochSecond(time).atZone(ZoneId.systemDefault()), exceptionPacket.getMetadata().packetID, exception.message);
    }
}
