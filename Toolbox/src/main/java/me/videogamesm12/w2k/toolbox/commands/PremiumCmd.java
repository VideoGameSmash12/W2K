package me.videogamesm12.w2k.toolbox.commands;

import com.google.gson.JsonParseException;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.toolbox.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.FileNotFoundException;

@Parameters(name = "premium", usage = "/<command> <name or UUID>")
public class PremiumCmd extends WCommand
{
    @ExecutionPath("<username or UUID|w2k:online_players/both>")
    public void checkPremium(final String nameOrUuid)
    {
        ProfileUtil.getMojangAPIData(nameOrUuid).whenComplete((result, ex) ->
        {
            if (ex != null)
            {
                if (ex instanceof FileNotFoundException)
                {
                    msg(Component.translatable("w2k.toolbox.ashcon.not_premium", Component.text(nameOrUuid, NamedTextColor.WHITE))
                            .color(NamedTextColor.GRAY));
                }
                else if (ex instanceof JsonParseException)
                {
                    msg(Component.translatable("w2k.toolbox.ashcon.error.bad_json", NamedTextColor.RED));
                }
                else
                {
                    msg(Component.translatable("w2k.toolbox.ashcon.error.unknown", NamedTextColor.RED));
                    W2K.getLogger().error("Details of the error: ", ex);
                }
                return;
            }

            msg(Component.translatable("w2k.toolbox.ashcon.premium", Component.text(result.getUsername(), NamedTextColor.WHITE))
                    .color(NamedTextColor.GRAY));
        });
    }
}
