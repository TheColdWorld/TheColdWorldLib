package cn.thecoldworld.thecoldworldlib.mixins.client;

import cn.thecoldworld.thecoldworldlib.interfaces.mixin.IGetClientConnection;
import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.HandShakePacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin implements IGetClientConnection {
    @Shadow
    volatile ClientConnection connection;


    @Override
    public ClientConnection getConnection() {
        if (connection == null) throw new IllegalStateException("don't connected");
        return connection;
    }

    @Mixin(targets = "net.minecraft.client.gui.screen.multiplayer.ConnectScreen$1")
    private static class connect {
        @Final
        @Shadow
        ConnectScreen field_2416;

        @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.BEFORE))
        private void AfterConnect(CallbackInfo ci) {
            ((IGetClientConnection) field_2416).getConnection().send(new HandShakePacket());
        }
    }
}
