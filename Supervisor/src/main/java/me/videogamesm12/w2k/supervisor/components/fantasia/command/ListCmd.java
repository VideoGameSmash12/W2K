package me.videogamesm12.w2k.supervisor.components.fantasia.command;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayerListEntryInterface;
import me.videogamesm12.w2k.supervisor.components.fantasia.session.CommandSender;

import java.util.Optional;
import java.util.stream.Collectors;

public class ListCmd extends FCommand
{
    public ListCmd()
    {
        super("list", "Get a list of all online players.", null);
    }

    @Override
    public boolean run(CommandSender sender, String[] args)
    {
        final Optional<PlayNetworkHandlerInterface> optionalHandler = W2K.getInstance().getVersionAbstractionLayer().networkHandler();

        if (!optionalHandler.isPresent())
        {
            sender.sendMessage("You are not connected to a server.");
            return true;
        }

        final PlayNetworkHandlerInterface handler = optionalHandler.get();

        if (handler.w2k$getOnlinePlayers().isEmpty())
        {
            sender.sendMessage("There are no players online.");
            return true;
        }

        sender.sendMessage("Currently online players:");
        sender.sendMessage(handler.w2k$getOnlinePlayers().stream()
                .map(PlayerListEntryInterface::w2k$username)
                .collect(Collectors.joining(", ")));
        return true;
    }
}
