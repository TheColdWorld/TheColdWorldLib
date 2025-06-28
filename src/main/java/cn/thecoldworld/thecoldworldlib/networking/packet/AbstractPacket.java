package cn.thecoldworld.thecoldworldlib.networking.packet;

import net.minecraft.network.listener.PacketListener;

public abstract class AbstractPacket<T extends PacketListener> implements net.minecraft.network.packet.Packet<T> {

}
