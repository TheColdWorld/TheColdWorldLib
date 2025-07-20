package cn.thecoldworld.thecoldworldlib.networking;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.submod.Mod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.slf4j.Logger;


public final class CommonPacketMetadata {
    public final Identifier packetID;

    private CommonPacketMetadata(Mod mod, String packetpath) {
        packetID = Identifier.of(mod.modid, packetpath);
    }

    public static final class ServerBind<T> {
        public static final Codec<ServerBind<?>> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Mod.CODEC.fieldOf("mod").forGetter(metadata -> metadata.mod),
                        Identifier.CODEC.fieldOf("packetid").forGetter(metadata -> metadata.packetID)
                ).apply(instance, ServerBind::new)
        );
        public final Mod mod;
        public final Identifier packetID;
        public final Callback<T> callback;
        public final Codec<T> codec;

        private ServerBind(Mod mod, Identifier packetID) {
            this.mod = mod;
            this.packetID = packetID;
            callback = (a, b, c, d, e) -> {
                throw new IllegalCallerException();
            };
            this.codec = (Codec<T>) Vars.DEFAULT_CODEC;
        }

        public ServerBind(Mod mod, String packetpath, Callback<T> callback, Codec<T> codec) {
            this.mod = mod;
            this.codec = codec;
            this.packetID = Identifier.of(mod.modid, packetpath);
            this.callback = callback;
        }

        @FunctionalInterface
        public interface Callback<T> {
            void onReply(MinecraftServer server, ServerPlayerEntity player, ClientConnection clientConnection, Logger logger, T content) throws Exception;
        }
    }

    public static final class ClientBind<T> {
        public static final Codec<ClientBind<?>> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Mod.CODEC.fieldOf("mod").forGetter(metadata -> metadata.mod),
                        Identifier.CODEC.fieldOf("packetid").forGetter(metadata -> metadata.packetID)
                ).apply(instance, ClientBind::new)
        );
        public final Mod mod;
        public final Identifier packetID;
        public final Callback<T> callback;
        public final Codec<T> codec;

        private ClientBind(Mod mod, Identifier packetID) {
            this.mod = mod;
            this.packetID = packetID;
            callback = (a, b, c, d, e) -> {
                throw new IllegalCallerException();
            };
            this.codec = (Codec<T>) Vars.DEFAULT_CODEC;
        }

        public ClientBind(Mod mod, String packetpath, Callback<T> callback, Codec<T> codec) {
            this.mod = mod;
            this.codec = codec;
            this.packetID = Identifier.of(mod.modid, packetpath);
            this.callback = callback;
        }

        @FunctionalInterface
        public interface Callback<T> {
            void onReply(World clientWorld, ClientConnection connection, Random random, Logger logger, T content) throws Exception;
        }
    }
}
