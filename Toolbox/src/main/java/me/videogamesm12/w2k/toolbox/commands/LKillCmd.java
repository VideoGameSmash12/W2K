package me.videogamesm12.w2k.toolbox.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.command.EntitySelectorInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import net.kyori.adventure.text.Component;

import java.util.List;

@Parameters(name = "lkill", usage = "")
public class LKillCmd extends WCommand
{
    @ExecutionPath("<selector|w2k:wrapped/entities>")
    public void kill(final EntitySelectorInterface selector)
    {
        // TODO: Build the self-exclusion into the resolver itself as a mode, lol
        final List<EntityInterface> entries = selector.w2k$getClientEntities();
        W2K.getInstance().getVersionAbstractionLayer().getLocalPlayer().ifPresent(player ->
                entries.removeIf(entry -> entry.w2k$uuid().equals(player.w2k$uuid())));
        int amount = 0;

        for (EntityInterface entry : entries)
        {
            amount++;
            entry.w2k$kill();
        }

        msg(Component.translatable("w2k.toolbox.lkill." + (amount == 1 ? "singular" : "plural"), Component.text(amount)));
    }
}
