package me.videogamesm12.w2k.toolbox;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.WCommandManager;
import me.videogamesm12.w2k.toolbox.commands.*;
import net.fabricmc.api.ClientModInitializer;

public class Toolbox implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        final WCommandManager commandManager = W2K.getInstance().getCommandManager();

        commandManager.registerCommand(NameCmd.class);
        commandManager.registerCommand(UuidCmd.class);
        commandManager.registerCommand(PremiumCmd.class);
        commandManager.registerCommand(DumpCmd.class);
        commandManager.registerCommand(LKillCmd.class);
    }
}
