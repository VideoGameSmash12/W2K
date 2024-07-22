package me.videogamesm12.w2k.drivers.v1_13.required;

import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WEventPassThruDriver;

@WDriverMetadata(identifier = "113_event_passthru")
public class W113EventPassThruDriver implements WEventPassThruDriver
{
    @Override
    public void setupStartedEvent()
    {
    }

    @Override
    public void setupStoppedEvent()
    {
    }
}
