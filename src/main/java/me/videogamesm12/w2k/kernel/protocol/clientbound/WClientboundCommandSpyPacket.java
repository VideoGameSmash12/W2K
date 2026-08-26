package me.videogamesm12.w2k.kernel.protocol.clientbound;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.protocol.WPacket;

import java.util.UUID;

@Getter
@WPacket.PacketMeta(id = {"w2k", "clientbound/command_spy"}, direction = WPacket.PacketMeta.Direction.CLIENT_BOUND)
public class WClientboundCommandSpyPacket extends WPacket
{
    private final UUID uuid;
    private final String username;
    private final String command;

    public WClientboundCommandSpyPacket(final UUID uuid, final String username, final String command)
    {
        super(-1);
        this.uuid = uuid;
        this.username = username;
        this.command = command;
    }
}
