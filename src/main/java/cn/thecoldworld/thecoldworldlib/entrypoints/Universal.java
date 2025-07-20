package cn.thecoldworld.thecoldworldlib.entrypoints;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.debug.DebugMessage;
import cn.thecoldworld.thecoldworldlib.debug.DebugVars;
import cn.thecoldworld.thecoldworldlib.networking.CommonPacketMetadata;
import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.CommonPlayC2SPacket;
import cn.thecoldworld.thecoldworldlib.networking.packet.s2c.CommonPlayS2CPacket;
import cn.thecoldworld.thecoldworldlib.submod.ModManager;
import cn.thecoldworld.thecoldworldlib.submod.OptionalMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import java.net.URI;
import java.net.URISyntaxException;

public class Universal implements ModInitializer {

    @Override
    public void onInitialize() {
        ModManager.initialize();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            try {
                OptionalMod mod = new OptionalMod(Vars.MODID, "=0.0.3", new URI("https://modrinth.com/project/thecoldworldlib"));
                ModManager.getInstance().register(mod);
                DebugVars.S2CMetadata = CommonPlayS2CPacket.register(new CommonPacketMetadata.ClientBind<>(mod, "networking/s2c/play/debug/common",
                        (world, connection, random, logger, content) -> {
                            logger.info("side {} send message:{}", content.Side(), content.Message());
                            try {//gen 3 depth exception
                                try {
                                    throw new IllegalArgumentException("Fuck you!(3)");
                                } catch (Exception e) {
                                    throw new AssertionError("a?(2)", e);
                                }
                            } catch (AssertionError e) {
                                throw new Exception("man!(1)", e);
                            }
                        }, RecordCodecBuilder.create(instance -> instance.group(
                        Codec.STRING.fieldOf("side").forGetter(DebugMessage::Side),
                        Codec.STRING.fieldOf("message").forGetter(DebugMessage::Message)
                ).apply(instance, DebugMessage::new))));
                DebugVars.C2SMetadata = CommonPlayC2SPacket.register(new CommonPacketMetadata.ServerBind<>(mod, "networking/c2s/play/debug/common",
                        (server, player, clientConnection, logger, content) -> {
                            logger.info("side {} send message:{}", content.Side(), content.Message());
                            try {//gen 3 depth exception
                                try {
                                    throw new IllegalArgumentException("ha?(3)");
                                } catch (Exception e) {
                                    throw new AssertionError("no?(2)", e);
                                }
                            } catch (AssertionError e) {
                                throw new Exception("what!(1)", e);
                            }
                        }, RecordCodecBuilder.create(instance -> instance.group(
                        Codec.STRING.fieldOf("side").forGetter(DebugMessage::Side),
                        Codec.STRING.fieldOf("message").forGetter(DebugMessage::Message)
                ).apply(instance, DebugMessage::new))));
            } catch (VersionParsingException | URISyntaxException e) {
                throw new CrashException(new CrashReport("", e));
            }
        }
    }
}
