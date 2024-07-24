package me.videogamesm12.w2k.kernel.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.data.BuildMetadata;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Parameters(name = "w2k", usage = "/<command> [details | experiments]")
public class W2KCmd extends WCommand
{
    @Override
    public boolean executeCommand(String commandLabel, String[] args)
    {
        if (args.length == 0)
        {
            FabricLoader.getInstance().getModContainer("w2k").ifPresent(container ->
            {
                msg(Component.translatable("w2k.command.w2k.info",
                        Component.text(container.getMetadata().getName()).color(NamedTextColor.WHITE),
                        Component.text(container.getMetadata().getVersion().getFriendlyString()).color(NamedTextColor.WHITE))
                        .colorIfAbsent(NamedTextColor.GRAY));
            });

            final BuildMetadata metadata = BuildMetadata.getMetadataFromClassJar(W2K.class);
            if (metadata != null)
            {
                msg(Component.translatable("w2k.command.w2k.click_to_see_build_info").color(NamedTextColor.BLUE)
                        .decorate(TextDecoration.UNDERLINED).clickEvent(ClickEvent.runCommand("/" + commandLabel + " details")));
            }
        }
        else
        {
            if (args[0].equalsIgnoreCase("details"))
            {
                final BuildMetadata metadata = BuildMetadata.getMetadataFromClassJar(W2K.class);
                if (metadata == null)
                {
                    msg(Component.translatable("w2k.command.w2k.unable_to_fetch_build_data").color(NamedTextColor.RED));
                }
                else
                {
                    msg(metadata.toComponent());
                }
            }
            else
            {
                return false;
            }
        }

        return true;
    }
}
