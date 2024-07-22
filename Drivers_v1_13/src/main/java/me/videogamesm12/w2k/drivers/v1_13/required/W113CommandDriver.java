package me.videogamesm12.w2k.drivers.v1_13.required;

import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.driver.base.WCommandDriver;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;

@WDriverMetadata(identifier = "113_command_wrapper")
public class W113CommandDriver implements WCommandDriver
{
    @Override
    public void registerCommand(WCommand command)
    {
        // Do nothing, we do all the work behind the scenes.
    }
}
