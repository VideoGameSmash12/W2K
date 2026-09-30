package me.videogamesm12.w2k.toolbox.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayerListEntryInterface;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.WCommand;
import net.kyori.adventure.text.Component;

public class ProfileCmd extends WCommand
{
    public ProfileCmd()
    {
        super("profile");
    }

    @ExecutionPath({"properties", "<username or UUID|w2k:online_players/both>"})
    public void properties(final String nameOrUuid)
    {
        W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                .flatMap(handler -> handler.w2k$getOnlinePlayers().stream()
                        .filter(entry -> entry.w2k$uuid().toString().equalsIgnoreCase(nameOrUuid) || entry.w2k$username().equalsIgnoreCase(nameOrUuid))
                        .findAny())
                .map(PlayerListEntryInterface::w2k$profile)
                .ifPresent(profile ->
                {
                    msg(Component.text("Properties for profile " + profile.w2k$name() + ":"));
                    profile.w2k$properties().forEach((name, property) ->
                            msg(Component.text(name + ": " + property.w2k$value())));
                });
    }
}
