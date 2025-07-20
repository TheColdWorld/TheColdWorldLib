package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.interfaces.mixin.IHandshakePacketAccessor;
import cn.thecoldworld.thecoldworldlib.interfaces.mixin.IModManagerAccessor;
import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.HandShakePacket;
import cn.thecoldworld.thecoldworldlib.submod.ModManager;
import cn.thecoldworld.thecoldworldlib.submod.OptionalMod;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    public void afterPLayerJoin(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        LinkedList<Text> texts = new LinkedList<>();
        HandShakePacket handShakePacket = ((IHandshakePacketAccessor) connection).getHandshakePacket();
        if (ModManager.getInstance().stream().filter(m -> m instanceof OptionalMod).findAny().isEmpty()) return;
        List<OptionalMod> cOptionalMods = handShakePacket.manager.stream().filter(m -> m instanceof OptionalMod).map(m -> (OptionalMod) m).collect(Collectors.toList());
        ModManager.getInstance().stream().filter(m -> m instanceof OptionalMod).map(m -> (OptionalMod) m)
                .filter(mod -> {
                    if (cOptionalMods.isEmpty()) {
                        return true;
                    }
                    OptionalMod willrm = null;
                    for (OptionalMod cOptionalMod : cOptionalMods) {
                        if (mod.isCompatible(cOptionalMod)) {
                            willrm = cOptionalMod;
                            break;
                        }
                    }
                    if (willrm != null) {
                        cOptionalMods.remove(willrm);
                        return true;
                    } else return false;
                }).forEach(m -> texts.add(m.createOptionalModInstallMessage()));
        if (texts.isEmpty()) return;
        MutableText maintext = Text.translatable("chat.thecoldworldlib.optionalmod", texts.size());
        texts.forEach(maintext::append);
        connection.send(new GameMessageS2CPacket(maintext, false));
        ((IModManagerAccessor) player).setModManager(handShakePacket.manager);
    }
}
