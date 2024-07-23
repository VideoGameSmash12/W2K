package me.videogamesm12.w2k.drivers.v1_15.required;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WEventPassThruDriver;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;

@WDriverMetadata(identifier = "15_event_passthru")
public class W115EventPassthruDriver implements WEventPassThruDriver, ClientLifecycleEvents.ClientStarted, ClientLifecycleEvents.ClientStopping
{
    @Override
    public void setupStartedEvent()
    {
        ClientLifecycleEvents.CLIENT_STARTED.register(this);
    }

    @Override
    public void setupStoppedEvent()
    {
        ClientLifecycleEvents.CLIENT_STOPPING.register(this);
    }

    @Override
    public void onClientStarted(MinecraftClient client)
    {
        W2K.getEventBus().post(new ClientStartedEvent(client));
    }

    @Override
    public void onClientStopping(MinecraftClient client)
    {
        W2K.getEventBus().post(new ClientStoppedEvent(client));
    }
}
