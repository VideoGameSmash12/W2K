package me.videogamesm12.w2k.toolbox.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.data.IEntityEntry;
import me.videogamesm12.w2k.kernel.data.IEntitySelector;
import net.kyori.adventure.text.Component;

import java.util.List;

@Parameters(name = "lkill", usage = "")
public class LKillCmd extends WCommand
{
    @ExecutionPath("<selector|w2k:wrapped/entities>")
    public void kill(final IEntitySelector selector)
    {
        // TODO: Build the self-exclusion into the resolver itself as a mode, lol
        final List<IEntityEntry> entries = selector.w2k$getClientEntities();
        entries.removeIf(entry -> entry.w2k$internalName().equalsIgnoreCase(W2K.getInstance().getDriverManager().getVersionBridge().getCurrentUsername()));
        int amount = 0;

        for (IEntityEntry entry : entries)
        {
            amount++;
            entry.w2k$kill();
        }

        msg(Component.translatable("w2k.toolbox.lkill." + (amount == 1 ? "singular" : "plural"), Component.text(amount)));
    }

    @Override
    public boolean executeCommand(String commandLabel, String[] args)
    {
        throw new UnsupportedOperationException("This command is only available with the new command system.");
    }
}
