package me.videogamesm12.w2k.kernel.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.data.BuildMetadata;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.module.WModule;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
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

    // -- Modules -- //

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

    // -- Experiments -- //

    @ExecutionPath("experiment")
    public void experimentSummary()
    {
        List<Experiment> enabled = ExperimentManager.getEnabledExperiments();

        if (enabled.isEmpty())
        {
            // "No experiments are enabled."
            msg(Component.translatable("w2k.command.experiments.none_enabled").color(NamedTextColor.RED));
        }
        else
        {
            // "X experiments are enabled."
            msg(Component.translatable("w2k.command.experiments.enabled",
                    Component.text(enabled.size())).decorate(TextDecoration.BOLD));

            // "Enabled: X, Y, Z"
            msg(Component.translatable("w2k.command.experiments.enabled_list",
                    Component.join(JoinConfiguration.commas(true),
                            enabled.stream().map(experiment -> Component.translatable(experiment.getTranslatableName()).color(NamedTextColor.GREEN))
                                    .collect(Collectors.toList()))).colorIfAbsent(NamedTextColor.GRAY));
            // "Disabled: A, B, C"
            msg(Component.translatable("w2k.command.experiments.disabled_list",
                    Component.join(JoinConfiguration.commas(true),
                            ExperimentManager.getRegisteredExperiments().stream().filter(ex -> !enabled.contains(ex))
                                    .map(experiment -> Component.translatable(experiment.getTranslatableName()).color(NamedTextColor.RED))
                                    .collect(Collectors.toList()))).colorIfAbsent(NamedTextColor.GRAY));
        }

            /* "To enable/disable experiments, you need to add '-Dme.videogamesm12.w2k.enabled_experiments='
               (with comma separated experiment names appended afterward) to your game's Java launch options.
               For more information, please consult the W2K wiki at https://github.com/VideoGameSmash12/W2K/wiki/Experiments." */
        msg(Component.translatable("w2k.command.experiments.hover_for_more_information")
                .hoverEvent(HoverEvent.showText(Component.translatable("w2k.command.experiments.instructions",
                                Component.text("\"-Dme.videogamesm12.w2k.enabled_experiments=\"")
                                        .color(NamedTextColor.WHITE),
                                Component.text("/experiments enable/disable <experiment>")
                                        .clickEvent(ClickEvent.suggestCommand("/experiments enable "))
                                        .color(NamedTextColor.WHITE),
                                Component.text("https://github.com/VideoGameSmash12/W2K/wiki/Experiments")
                                        .color(NamedTextColor.BLUE)
                                        .decorate(TextDecoration.UNDERLINED)
                                        .clickEvent(ClickEvent.openUrl("https://github.com/VideoGameSmash12/W2K/wiki/Experiments")))
                        .colorIfAbsent(NamedTextColor.GRAY)))
                .colorIfAbsent(NamedTextColor.BLUE).decorate(TextDecoration.UNDERLINED));
    }

    @ExecutionPath({"experiment", "list"})
    public void listAll()
    {
        // All Experiments:
        msg(Component.translatable("w2k.command.experiments.all_experiments").decorate(TextDecoration.BOLD));

        // A, B, C
        msg(Component.join(JoinConfiguration.commas(true), ExperimentManager.getRegisteredExperiments().stream()
                .map(experiment -> Component.translatable(experiment.getTranslatableName()).color(ExperimentManager.isExperimentEnabled(experiment)
                                ? NamedTextColor.GREEN : NamedTextColor.RED).hoverEvent(HoverEvent.showText(
                                Component.translatable("w2k.command.experiments.click_for_more_information")))
                        .clickEvent(ClickEvent.runCommand("/experiments details " + experiment.getIdentifier())))
                .collect(Collectors.toList())).colorIfAbsent(NamedTextColor.GRAY));
    }

    @ExecutionPath({"experiment", "details"})
    public void redirectSummary()
    {
        summary();
    }

    @ExecutionPath({"experiment", "details", "<experiment|w2k:experiment/all>"})
    public void experimentDetails(final Experiment experiment)
    {
        msg(Component.translatable("w2k.command.experiments.experiment_details")
                .decorate(TextDecoration.BOLD));
        // Name: %s
        msg(Component.translatable("w2k.command.experiments.experiment_details_name",
                experiment.getName().color(NamedTextColor.WHITE)).colorIfAbsent(NamedTextColor.GRAY));
        // Description: %s
        msg(Component.translatable("w2k.command.experiments.experiment_details_description",
                experiment.getDescription().color(NamedTextColor.WHITE)).colorIfAbsent(NamedTextColor.GRAY));
    }

    @ExecutionPath({"experiment", "toggle", "<experiment|w2k:experiment/togglable>"})
    public void experimentToggle(final Experiment experiment)
    {
        experimentSet(experiment, !ExperimentManager.isExperimentEnabled(experiment));
    }

    @ExecutionPath({"experiment", "set", "<experiment|w2k:experiment/togglable>", "<value|brigadier:bool>"})
    public void experimentSet(final Experiment experiment, final boolean value)
    {
        if (ExperimentManager.isExperimentEnabled(experiment) == value)
        {
            msg(Component.translatable("w2k.command.experiments.already_" + (value ? "enabled" : "disabled")));
            return;
        }

        if (value)
        {
            ExperimentManager.enableExperiment(experiment);
        }
        else
        {
            ExperimentManager.disableExperiment(experiment);
        }

        msg(Component.translatable("w2k.command.experiments.experiment_" + (value ? "enabled" : "disabled"),
                        Component.text(experiment.getIdentifier()).color(NamedTextColor.DARK_GREEN))
                .color(NamedTextColor.GREEN));
    }

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
