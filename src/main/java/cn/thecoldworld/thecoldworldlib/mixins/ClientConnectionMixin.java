package cn.thecoldworld.thecoldworldlib.mixins;

import cn.thecoldworld.thecoldworldlib.interfaces.mixin.IHandshakePacketAccessor;
import cn.thecoldworld.thecoldworldlib.networking.packet.c2s.HandShakePacket;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements IHandshakePacketAccessor {
    @Unique
    HandShakePacket handShakePacket = null;

    @Override
    public HandShakePacket getHandshakePacket() {
        return handShakePacket;
    }

    @Override
    public void setHandshakePacket(HandShakePacket handShakePacket) {
        this.handShakePacket = handShakePacket;
    }
}
