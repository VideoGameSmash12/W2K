package me.videogamesm12.w2k.toolbox.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.driver.base.WAmbassadorDriver;
import me.videogamesm12.w2k.kernel.protocol.common.WCommonErrorPacket;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.lang.reflect.InvocationTargetException;

@Parameters(name = "debug", usage = "/debug")
public class DebugCmd extends WCommand
{
    @Override
    public boolean executeCommand(String commandLabel, String[] args)
    {
        final WAmbassadorDriver communicationsDriver = W2K.getInstance().getDriverManager().getCommunicationsDriver();

        if (communicationsDriver != null)
        {
            communicationsDriver.sendPacket(new WCommonErrorPacket(420, WCommonErrorPacket.Error.UNKNOWN_ERROR, "lmao"));
            msg(Component.text("Packet sent", NamedTextColor.GREEN));
        }
        else
        {
            msg(Component.text("Communications driver is null", NamedTextColor.RED));
        }

        return true;
    }
}
