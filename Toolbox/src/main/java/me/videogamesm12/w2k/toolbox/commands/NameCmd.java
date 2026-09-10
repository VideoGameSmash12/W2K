package me.videogamesm12.w2k.toolbox.commands;

import com.google.gson.JsonParseException;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.Argument;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.toolbox.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.io.FileNotFoundException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Parameters(name = "name", usage = "/<command> <uuid>")
public class NameCmd extends WCommand
{
    @ExecutionPath
    public void fetchName(final @Argument(label = "uuid", resolver = "w2k:online_players/uuid") UUID uuid)
    {
        ProfileUtil.getAshconDataAsync(uuid.toString()).whenComplete((result, ex) ->
        {
            if (ex != null)
            {
                if (ex instanceof FileNotFoundException)
                {
                    msg(Component.translatable("w2k.toolbox.ashcon.error.player_not_found", NamedTextColor.RED));
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

            msg(Component.translatable("w2k.toolbox.ashcon.result.uuid",
                            Component.text(result.getUsername())
                                    .color(NamedTextColor.WHITE),
                            Component.text(result.getUuid())
                                    .color(NamedTextColor.WHITE)
                                    .decorate(TextDecoration.UNDERLINED)
                                    .clickEvent(ClickEvent.copyToClipboard(result.getUuid()))
                                    .hoverEvent(HoverEvent.showText(Component.translatable("chat.copy.click"))))
                    .colorIfAbsent(NamedTextColor.GRAY));
        });
    }

    @Override
    public boolean executeCommand(String commandLabel, String[] args)
    {
        if (args.length == 0)
        {
            return false;
        }

        CompletableFuture.supplyAsync(() ->
        {
            try
            {
                return ProfileUtil.getAshconData(UUID.fromString(args[0].toLowerCase()).toString());
            }
            catch (FileNotFoundException ex)
            {
                msg(Component.translatable("w2k.toolbox.ashcon.error.player_not_found", NamedTextColor.RED));
            }
            catch (JsonParseException ex)
            {
                msg(Component.translatable("w2k.toolbox.ashcon.error.bad_json", NamedTextColor.RED));
            }
            catch (IllegalArgumentException ex)
            {
                msg(Component.translatable("w2k.toolbox.ashcon.error.not_a_uuid",
                        Component.text(args[0].toLowerCase())).color(NamedTextColor.RED));
            }
            catch (Throwable ex)
            {
                msg(Component.translatable("w2k.toolbox.ashcon.error.unknown", NamedTextColor.RED));
                W2K.getLogger().error("Details of the error: ", ex);
            }

            return null;
        }).whenComplete((result, ex) ->
        {
            if (result == null) return;

            msg(Component.translatable("w2k.toolbox.ashcon.result.name",
                    Component.text(result.getUuid())
                            .color(NamedTextColor.WHITE),
                    Component.text(result.getUsername())
                            .color(NamedTextColor.WHITE)
                            .decorate(TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.copyToClipboard(result.getUsername()))
                            .hoverEvent(HoverEvent.showText(Component.translatable("chat.copy.click"))))
                    .colorIfAbsent(NamedTextColor.GRAY));
        });

        return true;
    }
}
