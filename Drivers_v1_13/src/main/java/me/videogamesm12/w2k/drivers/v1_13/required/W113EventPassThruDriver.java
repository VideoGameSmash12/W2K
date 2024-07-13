package me.videogamesm12.w2k.drivers.v1_13.required;

import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WEventPassThruDriver;

@WDriverMetadata(identifier = "113_event_passthru", maxVersion = "1.13.2", minVersion = "1.13.2", minProtocolVersion = 0, maxProtocolVersion = 0)
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
