package me.videogamesm12.w2k.toolbox.modules;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.kernel.event.network.DisconnectEvent;
import me.videogamesm12.w2k.kernel.event.render.OverlayRequestEvent;
import me.videogamesm12.w2k.kernel.graphics.core.TextLabel;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundHeartbeatPacket;
import net.kyori.adventure.text.Component;

import java.util.Arrays;

public class TPSOverlay extends WModule
{
    private final TextLabel overlayTopLeft = addDrawableOverlay(new TextLabel(Component.empty(), 0, 0));

    public final double[] ticks = new double[]{0, 0, 0};

    public TPSOverlay()
    {
        super("tps_overlay",
                "TPS Overlay",
                "Show an overlay of the average tick rate of the server. Requires LNX or a plugin implementing the WCom standard to be installed on the server-side.",
                null);

        registerEventDispatcher(w2k().getCommunicationManager().getEventBus());
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event)
    {
        overlayTopLeft.setSource(Component.empty());
    }

    @Subscribe
    public void onHeartbeatPacket(WClientboundHeartbeatPacket packet)
    {
        ticks[0] = packet.getOneMinute();
        ticks[1] = packet.getFiveMinutes();
        ticks[2] = packet.getTenMinutes();

        overlayTopLeft.setSource(getTPSText());
    }

    @Subscribe
    public void onOverlayRequest(OverlayRequestEvent event)
    {
        event.submitAll(getDrawableOverlays());
    }

    public Component getTPSText()
    {
        return Component.text("TPS: " + Arrays.toString(ticks));
    }
}
