package me.videogamesm12.w2k.kernel.protocol.serverbound;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.protocol.WPacket;

@Getter
@WPacket.PacketMeta(id = {"w2k", "serverbound/command"},
        direction = WPacket.PacketMeta.Direction.SERVER_BOUND)
public class WServerboundCommandPacket extends WPacket
{
    private final String message;

    public WServerboundCommandPacket(final long transactionId, final String message)
    {
        super(transactionId);
        this.message = message;
    }
}
