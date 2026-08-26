package me.videogamesm12.w2k.toolbox;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.toolbox.commands.DebugCmd;
import me.videogamesm12.w2k.toolbox.commands.DumpCmd;
import me.videogamesm12.w2k.toolbox.commands.NameCmd;
import me.videogamesm12.w2k.toolbox.commands.UuidCmd;
import net.fabricmc.api.ClientModInitializer;

public class Toolbox implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        W2K.getInstance().getCommandManager().registerCommand(NameCmd.class);
        W2K.getInstance().getCommandManager().registerCommand(UuidCmd.class);
        W2K.getInstance().getCommandManager().registerCommand(DumpCmd.class);
        W2K.getInstance().getCommandManager().registerCommand(DebugCmd.class);
    }
}
