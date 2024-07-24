package me.videogamesm12.w2k.blackbox.command;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.swing.*;

@Parameters(name = "blackbox", usage = "/<command> <open | status>")
public class BlackboxCmd extends WCommand
{
    @Override
    public boolean executeCommand(String commandLabel, String[] args)
    {
        if (args.length == 0)
        {
            return false;
        }

        switch (args[0].toLowerCase())
        {
            case "open":
            {
                msg(Component.translatable("w2k.blackbox.command.show").color(NamedTextColor.GREEN));
                SwingUtilities.invokeLater(() -> Blackbox.getInstance().openWindow());
                break;
            }
            case "status":
            {
                Component status = (Blackbox.getInstance().getMainWindow() != null ? (Blackbox.getInstance().getMainWindow().isVisible() ?
                        Component.translatable("w2k.blackbox.command.status.inMemoryAndVisible")
                        : Component.translatable("w2k.blackbox.command.status.inMemoryButNotVisible"))
                        : Component.translatable("w2k.blackbox.command.status.notInMemory")).color(NamedTextColor.WHITE);

                msg(Component.translatable("w2k.blackbox.command.status", status).colorIfAbsent(NamedTextColor.GRAY));
                break;
            }
            default:
            {
                return false;
            }
        }

        return true;
    }
}
