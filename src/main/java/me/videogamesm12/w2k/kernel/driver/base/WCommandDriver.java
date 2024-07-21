package me.videogamesm12.w2k.kernel.driver.base;

import me.videogamesm12.w2k.kernel.command.WCommand;

import java.util.List;

public interface WCommandDriver extends WDriver
{
    default void registerCommands(List<WCommand> commands)
    {
        commands.forEach(this::registerCommand);
    }

    void registerCommand(WCommand commands);
}
