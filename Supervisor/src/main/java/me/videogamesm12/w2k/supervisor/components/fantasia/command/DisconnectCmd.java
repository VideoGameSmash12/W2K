package me.videogamesm12.w2k.supervisor.components.fantasia.command;

import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.fantasia.session.CommandSender;

public class DisconnectCmd extends FCommand
{
    public DisconnectCmd()
    {
        super("disconnect", "Disconnects you from the server you are currently connected to. Requires you to be connected to a server for this to work.", "disconnect");
    }

    @Override
    public boolean run(CommandSender sender, String[] args)
    {
        sender.sendMessage("Disconnecting from the server...");
        Supervisor.getInstance().disconnect();
        return true;
    }
}
