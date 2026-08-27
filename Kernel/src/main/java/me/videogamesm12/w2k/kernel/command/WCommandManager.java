package me.videogamesm12.w2k.kernel.command;

import me.videogamesm12.w2k.kernel.W2K;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h1>WCommandManager</h1>
 * <p>Manager for {@link WCommand} instances. This manager allows for version-agnostic client commands that behave
 * similarly to Bukkit's command system.</p>
 */
public class WCommandManager
{
    private final Map<String, WCommand> commandMap = new HashMap<>();

    /**
     * Returns whether a {@link me.videogamesm12.w2k.kernel.driver.base.WCommandDriver command wrapper driver} is
     *  currently registered
     * @return  True if a command driver is present.
     */
    public boolean isSupported()
    {
        return W2K.getInstance().getDriverManager().getCommandWrapper() != null;
    }

    /**
     * Register a WCommand instance.
     * @param command   {@link WCommand}
     */
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

    /**
     * Register a WCommand class as an instance. Classes registered this way must have the {@link Parameters} annotation
     * present.
     * @param command   {@code Class<WCommand>}
     */
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

    public List<String> getCommandNames()
    {
        return new ArrayList<>(commandMap.keySet());
    }

    public List<WCommand> getCommands()
    {
        return new ArrayList<>(commandMap.values());
    }

    public WCommand getCommand(String name)
    {
        return commandMap.get(name);
    }
}
