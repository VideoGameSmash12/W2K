package me.videogamesm12.w2k.kernel.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.data.BuildMetadata;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
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
                    msg(Component.translatable("%s", Component.translatable("w2k.command.w2k.build_info.header").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)).color(NamedTextColor.GRAY) // $#&^!
                            .append(Component.newline())
                            .append(Component.translatable("w2k.command.w2k.build_info.branch", Component.text(metadata.getBranch()).color(NamedTextColor.WHITE)))
                            .append(Component.newline())
                            .append(Component.translatable("w2k.command.w2k.build_info.commit_id",
                                    Component.text(metadata.getCommitId()).color(NamedTextColor.WHITE),
                                    Component.text(metadata.getCommitIdAbbreviated()).color(NamedTextColor.WHITE)))
                            .append(Component.newline())
                            .append(Component.translatable("w2k.command.w2k.build_info.commit_time",
                                    Component.text(metadata.getCommitTime()).color(NamedTextColor.WHITE)))
                            .append(Component.newline())
                            .append(Component.translatable("w2k.command.w2k.build_info.origin_url",
                                    Component.text(metadata.getOriginUrl()).color(NamedTextColor.WHITE)))
                            .append(Component.newline())
                            .append(Component.translatable("w2k.command.w2k.build_info.dirty",
                                    Component.text(metadata.isDirty()).color(NamedTextColor.WHITE)))
                            .hoverEvent(HoverEvent.showText(Component.translatable("chat.click.copy_to_clipboard")))
                            .clickEvent(ClickEvent.copyToClipboard(toString())));
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
