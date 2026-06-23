package me.videogamesm12.w2k.drivers.v26_1.required;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WEventPassThruDriver;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.NonNull;

@WDriverMetadata(identifier = "1212_event_passthru")
public class WEventPassthruDriver implements WEventPassThruDriver, ClientLifecycleEvents.ClientStarted, ClientLifecycleEvents.ClientStopping
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
    public void onClientStarted(@NonNull Minecraft client)
    {
        W2K.getEventBus().post(new ClientStartedEvent(client));
    }

    @Override
    public void onClientStopping(@NonNull Minecraft client)
    {
        W2K.getEventBus().post(new ClientStoppedEvent(client));
    }
}
