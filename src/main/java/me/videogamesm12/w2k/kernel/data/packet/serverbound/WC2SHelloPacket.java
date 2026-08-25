package me.videogamesm12.w2k.kernel.data.packet.serverbound;

import me.videogamesm12.w2k.kernel.data.packet.WPacketMeta;
import me.videogamesm12.w2k.kernel.data.packet.WPacket;

@WPacketMeta(value = "w2k:serverbound/hello", direction = WPacketMeta.Direction.SERVERBOUND)
public interface WC2SHelloPacket extends WPacket
{
    int protocolVersion();
}
