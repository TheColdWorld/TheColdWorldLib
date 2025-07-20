package cn.thecoldworld.thecoldworldlib.mixins.client;

import cn.thecoldworld.thecoldworldlib.exceptions.ClientBindPacketException;
import cn.thecoldworld.thecoldworldlib.exceptions.ClientBindPacketExceptionBase;
import cn.thecoldworld.thecoldworldlib.exceptions.SerializedException;
import cn.thecoldworld.thecoldworldlib.networking.CommonPacketMetadata;
import cn.thecoldworld.thecoldworldlib.networking.listeners.ClientPlayPacketListener;
import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.CommonPlayC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.time.Instant;
import java.time.ZoneId;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    private ClientWorld world;
    @Shadow
    @Final
    private Random random;

    @Shadow
    public abstract ClientConnection getConnection();

    @Override
    public <T> void TheColdWorldLib$common(CommonPacketMetadata.ClientBind<T> metadata, NbtCompound content) throws ClientBindPacketExceptionBase {
        try {
            metadata.callback.onReply(world, getConnection(), random, LOGGER, metadata.codec.decode(NbtOps.INSTANCE, content).getOrThrow().getFirst());
        } catch (IllegalCallerException e) {
            throw new CrashException(new CrashReport("internal exception:Illegal caller from packet" + metadata.packetID.toString(), e));
        } catch (Throwable throwable) {
            throw new ClientBindPacketException(MinecraftClient.getInstance(), world, getConnection(), random, throwable);
        }
    }

    @Override
    public <T> void TheColdWorldLib$exception(CommonPlayC2SPacket<T> exceptionPacket, SerializedException exception, long time) {
        LOGGER.error("Server occored exception {} at {} in package{},message{}", exception.className, Instant.ofEpochSecond(time).atZone(ZoneId.systemDefault()), exceptionPacket.getMetadata().packetID, exception.message);
    }
}
