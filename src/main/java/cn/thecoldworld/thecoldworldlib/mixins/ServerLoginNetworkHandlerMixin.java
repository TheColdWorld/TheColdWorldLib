package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.Vars;
import cn.thecoldworld.thecoldworldlib.interfaces.mixin.IHandshakePacketAccesser;
import cn.thecoldworld.thecoldworldlib.networking.listeners.ServerLoginPacketListener;
import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.HandShakePacket;
import cn.thecoldworld.thecoldworldlib.submod.ModManager;
import cn.thecoldworld.thecoldworldlib.submod.RequiredMod;
import com.mojang.authlib.GameProfile;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin implements ServerLoginPacketListener {
    @Shadow
    @Final
    ClientConnection connection;

    @Override
    @Unique
    public void TheColdWorldLib$OnHandShake(HandShakePacket packet) {
        ((IHandshakePacketAccesser) connection).setHandshakePacket(packet);
    }

    @Inject(method = "sendSuccessPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V"), cancellable = true)
    private void beforeReply(GameProfile profile, CallbackInfo ci) {
        Consumer<Text> disconnect = reason -> {
            this.connection.send(new LoginDisconnectS2CPacket(reason));
            this.connection.handleDisconnection();
        };
        HandShakePacket handShakePacket = ((IHandshakePacketAccesser) connection).getHandshakePacket();
        if (handShakePacket == null) {
            disconnect.accept(Text.literal("[TheColdWorldLib] mod TheColdWorldLib is not installed\nThis Server requires version " + Vars.MOD_VERSION + "\n install it from https://modrinth.com/project/thecoldworldlib"));
            ci.cancel();
        } else {
            if (!handShakePacket.isCompatible(FabricLoader.getInstance().getModContainer(Vars.MODID).get())) {
                disconnect.accept(Text.translatable("disconnect.thecoldworldlib.wrongversion", Vars.COMPATIBLE_VERSION, handShakePacket.VersionString));
                ci.cancel();
            } else {
                ModManager manager = ModManager.getInstance();
                handShakePacket.manager.stream().filter(m -> m instanceof RequiredMod).forEach(mod -> {
                    var opt = manager.get(mod.modid);
                    if (opt.isEmpty()) {
                        disconnect.accept(mod.createNotintalledText());
                        ci.cancel();
                        return;
                    }
                    if (!mod.isCompatible(opt.get())) {
                        disconnect.accept(mod.createWrongVersionText(opt.get().version));
                        ci.cancel();
                        return;
                    }
                });
            }
        }
    }
}
