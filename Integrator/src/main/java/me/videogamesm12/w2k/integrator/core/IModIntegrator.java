package me.videogamesm12.w2k.integrator.core;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;

public abstract class IModIntegrator
{
    @Subscribe
    private void onClientStarted(ClientStartedEvent event)
    {
        onStart();
    }

    @Subscribe
    private void onClientStopped(ClientStoppedEvent event)
    {
        onStop();
    }

    public void setup()
    {
        W2K.getEventBus().register(this);

        postSetup();
    }

    public void onStart()
    {
    }

    public void onStop()
    {
    }

    public void postSetup()
    {
    }
}
