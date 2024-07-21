package me.videogamesm12.w2k.kernel.command;

import me.videogamesm12.w2k.kernel.W2K;

import java.util.HashMap;
import java.util.Map;

public class WCommandManager
{
    private final Map<String, WCommand> commandMap = new HashMap<>();

    public boolean isSupported()
    {
        return W2K.getInstance().getDriverManager().getCommandWrapper() != null;
    }

    public void registerCommand(WCommand command)
    {
        // Don't register commands if the command driver doesn't exist
        if (!isSupported())
            return;

        if (isRegistered(command.getName()))
            throw new IllegalArgumentException("Command class has already been registered!");

        commandMap.put(command.getName(), command);
        W2K.getInstance().getDriverManager().getCommandWrapper().registerCommand(command);
    }

    public void registerCommand(Class<? extends WCommand> command)
    {
        try
        {
            registerCommand(command.getDeclaredConstructor().newInstance());
        }
        catch (Exception ex)
        {
            W2K.getLogger().error("Failed to register command {}", command.getName(), ex);
        }
    }

    /**
     * Checks to see whether a command class has been registered.
     * @param name   Name of the command
     * @return       True if the command is already registered
     */
    public boolean isRegistered(String name)
    {
        return commandMap.containsKey(name);
    }
}
