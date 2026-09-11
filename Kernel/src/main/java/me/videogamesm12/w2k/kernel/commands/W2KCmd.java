package me.videogamesm12.w2k.kernel.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.data.BuildMetadata;
import me.videogamesm12.w2k.kernel.module.WModule;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.stream.Collectors;

@Parameters(name = "w2k", usage = "/<command> [details]")
public class W2KCmd extends WCommand
{
    @ExecutionPath
    public void summary()
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
                    .decorate(TextDecoration.UNDERLINED).clickEvent(ClickEvent.runCommand("/w2k details")));
        }
    }

    @ExecutionPath("details")
    public void details()
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

    @ExecutionPath({"module", "toggle", "<module|w2k:module>"})
    public void toggleModule(final WModule module)
    {
        try
        {
            module.setEnabled(module.isEnabled());
            msg(Component.translatable("w2k.command.w2k.module.toggled",
                    Component.text(module.getName()).color(NamedTextColor.WHITE),
                    module.isEnabled() ?
                            Component.translatable("w2k.command.w2k.module.toggled.enabled", NamedTextColor.GREEN) :
                            Component.translatable("w2k.command.w2k.module.toggled.disabled", NamedTextColor.RED))
                    .color(NamedTextColor.GRAY));
        }
        catch (IllegalArgumentException ex)
        {
            msg(Component.translatable("w2k.command.w2k.module.cannot_be_toggled", NamedTextColor.RED));
        }
    }

    @ExecutionPath({"module", "status", "<module|w2k:module>"})
    public void moduleStatus(final WModule module)
    {
        msg(Component.translatable("w2k.command.w2k.module.status",
                Component.text(module.getName()).color(NamedTextColor.WHITE),
                module.isEnabled() ?
                        Component.translatable("w2k.command.w2k.module.status.enabled", NamedTextColor.GREEN) :
                        Component.translatable("w2k.command.w2k.module.status.disabled", NamedTextColor.RED))
                .color(NamedTextColor.GRAY));
    }

    /*@ExecutionPath("module setting")
    public void moduleSetting(final @Argument(label = "module") WModule module,
                              final @Argument(label = "setting name") WModuleSetting<?, ?> setting,
                              final @Argument(label = "value") String value)
    {
        setting.read(value);
    }*/

    @Override
    public boolean executeCommand(String commandLabel, String[] args)
    {
        if (args.length == 0)
        {
            summary();
        }
        else
        {
            if (args[0].equalsIgnoreCase("details"))
            {
                details();
            }
            else if (args[0].equalsIgnoreCase("module"))
            {
                if (args.length == 2 && args[1].equalsIgnoreCase("list"))
                {
                    msg(Component.text("Available modules: ", NamedTextColor.GRAY)
                            .append(Component.join(JoinConfiguration.commas(true),
                                    W2K.getInstance().getModuleManager().getIdRegistry().keySet().stream()
                                            .map(key -> Component.text(key, NamedTextColor.WHITE))
                                            .collect(Collectors.toList()))));
                    return true;
                }

                if (args.length != 3)
                {
                    return false;
                }

                final String moduleId = args[2];
                final WModule module = W2K.getInstance().getModuleManager().getModule(moduleId);

                if (module == null)
                {
                    msg(Component.text("Invalid module: " + moduleId, NamedTextColor.RED));
                    return true;
                }

                if (args[1].equalsIgnoreCase("toggle"))
                {
                    toggleModule(module);
                    return true;
                }
                else if (args[1].equalsIgnoreCase("status"))
                {
                    moduleStatus(module);
                    return true;
                }
            }

            return false;
        }

        return true;
    }
}
