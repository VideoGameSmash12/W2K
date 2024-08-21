package me.videogamesm12.w2k.kernel.driver.base;

import me.videogamesm12.w2k.kernel.command.WCommand;

import java.util.List;

/**
 * <h1>WCommandDriver</h1>
 * <p>A specific kind of WDriver which wraps {@link WCommand} instances and registers them as client commands in
 * existing APIs like the Fabric API.</p>
 */
public interface WCommandDriver extends WDriver
{
    default void registerCommands(List<WCommand> commands)
    {
        commands.forEach(this::registerCommand);
    }

    void registerCommand(WCommand commands);
}
