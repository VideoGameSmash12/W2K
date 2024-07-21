package me.videogamesm12.w2k.drivers.v1_12.required;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WEventPassThruDriver;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;

@WDriverMetadata(identifier = "18_event_passthru", maxVersion = "1.12.2", minVersion = "1.12.2", minProtocolVersion = 0, maxProtocolVersion = 0)
public class W112EventPassThruDriver implements WEventPassThruDriver
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
