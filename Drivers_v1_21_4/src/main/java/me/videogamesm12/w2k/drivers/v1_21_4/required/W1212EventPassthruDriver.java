package me.videogamesm12.w2k.drivers.v1_21_4.required;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WEventPassThruDriver;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.kernel.event.network.DisconnectEvent;
import me.videogamesm12.w2k.kernel.event.network.JoinEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@WDriverMetadata(identifier = "1212_event_passthru")
public class W1212EventPassthruDriver implements WEventPassThruDriver
{
    @Override
    public void setupLifecycleEvents()
    {
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> W2K.getEventBus().post(new ClientStartedEvent(client)));
        ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> W2K.getEventBus().post(new ClientStoppedEvent(client)));
    }

    @Override
    public void setupNetworkEvents()
    {
        ClientPlayConnectionEvents.DISCONNECT.register((connection, client) -> W2K.getEventBus().post(new DisconnectEvent(connection, client)));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> W2K.getEventBus().post(new JoinEvent(handler, sender, client)));
    }
}
