package me.videogamesm12.w2k.kernel.protocol.serverbound;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.protocol.Stage;
import me.videogamesm12.w2k.kernel.protocol.WPacket;

@Getter
@WPacket.PacketMeta(id = {"w2k", "serverbound/hello"},
        direction = WPacket.PacketMeta.Direction.SERVER_BOUND,
        stage = Stage.HELLO)
public class WServerboundHelloPacket extends WPacket
{
    private final int protocolVersion;
    private final String minecraftVersion;

    public WServerboundHelloPacket(final long transactionId, final int protocolVersion, final String minecraftVersion)
    {
        super(transactionId);
        this.protocolVersion = protocolVersion;
        this.minecraftVersion = minecraftVersion;
    }
}
