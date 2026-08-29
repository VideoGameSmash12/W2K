package me.videogamesm12.w2k.toolbox.modules;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.protocol.WPacketReceivedEvent;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundCommandSpyPacket;
import me.videogamesm12.wcom.protocol.common.WCommonErrorPacket;

public class WPacketLogger extends WModule
{
    public WPacketLogger()
    {
        super("Debug", "Testing some stuff");
    }

    @Subscribe
    public void onPacketReceived(WPacketReceivedEvent<WCommonErrorPacket> error)
    {
        W2K.getLogger().info(error.toString());
    }

    @Subscribe
    public void onCommandSpyPacket(WClientboundCommandSpyPacket commandSpyPacket)
    {
        W2K.getLogger().info("Received command from server - {} (UUID {}) executed server command {}", commandSpyPacket.getUsername(), commandSpyPacket.getUuid(), commandSpyPacket.getCommand());
    }
}
