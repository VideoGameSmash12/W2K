package me.videogamesm12.w2k.kernel.commands;

import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import net.kyori.adventure.text.Component;

@Parameters(name = "w2k", usage = "/<command>")
public class W2KCmd extends WCommand
{
    @Override
    public boolean executeCommand(String commandLabel, String[] args)
    {
        msg(Component.text("You're running W2K!"));
        return true;
    }
}
