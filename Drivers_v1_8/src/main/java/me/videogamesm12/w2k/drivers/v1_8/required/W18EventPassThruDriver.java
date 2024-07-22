package me.videogamesm12.w2k.drivers.v1_8.required;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WEventPassThruDriver;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

@WDriverMetadata(identifier = "18_event_passthru", requiredMods = "legacy-fabric-api")
public class W18EventPassThruDriver implements WEventPassThruDriver
{
    @Override
    public void setupStartedEvent()
    {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> W2K.getEventBus().post(new ClientStartedEvent(client)));
    }

    @Override
    public void setupStoppedEvent()
    {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> W2K.getEventBus().post(new ClientStoppedEvent(client)));
    }
}
