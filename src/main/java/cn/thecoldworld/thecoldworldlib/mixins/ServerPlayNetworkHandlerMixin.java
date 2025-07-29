package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.config.ConfigManager;
import cn.thecoldworld.thecoldworldlib.config.ExceptionConfig;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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
        ConfigManager manager = ConfigManager.getInstance();
        ExceptionConfig exceptionConfig = manager.getOrElse(ExceptionConfig.ID, ExceptionConfig.class, ExceptionConfig::new);
        if (exceptionConfig.is_WritePacketExceptionToFile()) {
            File file;
            if (Path.of(exceptionConfig.get_exceptionFileStoreDir()).isAbsolute()) {
                file = Path.of(exceptionConfig.get_exceptionFileStoreDir(),
                        Instant.ofEpochSecond(time).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-ss"))
                                + "-" + exception.className + "-Client.txt").toFile();
            } else
                file = Objects.requireNonNull(player.getServer()).getRunDirectory().resolve("TheColdWorldLib/exceptions/" +
                        Instant.ofEpochSecond(time).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-ss"))
                        + "-" + exception.className + "-Client.txt").toFile();
            try {
                exception.writeToFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        LOGGER.error("Client occurred exception {} at {} in packet {},message:{}", exception.className, Instant.ofEpochSecond(time).atZone(ZoneId.systemDefault()), exceptionPacket.getMetadata().packetID, exception.message);
    }
}
