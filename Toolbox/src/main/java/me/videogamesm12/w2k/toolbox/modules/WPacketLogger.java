package me.videogamesm12.w2k.toolbox.modules;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.protocol.WPacketReceivedEvent;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.protocol.common.WCommonErrorPacket;

public class WPacketLogger extends WModule
{
    public WPacketLogger()
    {
        super("Debug", "Testing some stuff");
    }

    @Subscribe
    public void onPacketReceieved(WPacketReceivedEvent<WCommonErrorPacket> error)
    {
        W2K.getLogger().info(error.toString());
    }
}
