package me.videogamesm12.w2k.toolbox.modules;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.protocol.WPacketReceivedEvent;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundHeartbeatPacket;

public class TPSOverlay extends WModule
{
    public final double[] ticks = new double[]{0, 0, 0};

    public TPSOverlay()
    {
        super("TPS Overlay (Not Working)",
                "Show an overlay of the average tick rate of the server.");
    }

    @Subscribe
    public void onHeartbeatPacket(WClientboundHeartbeatPacket packet)
    {
        ticks[0] = packet.getOneMinute();
        ticks[1] = packet.getFiveMinutes();
        ticks[2] = packet.getTenMinutes();

        W2K.getLogger().info("Debug - got tps update packet");
    }
}
