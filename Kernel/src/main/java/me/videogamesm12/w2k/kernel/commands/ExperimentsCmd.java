package me.videogamesm12.w2k.kernel.commands;

import me.videogamesm12.w2k.kernel.command.Argument;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Parameters(name = "experiments", usage = "/<command> <<details | set> <experiment> <value> | list>")
public class ExperimentsCmd extends WCommand
{
    @ExecutionPath
    public void summary()
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
                            enabled.stream().map(experiment -> Component.text(experiment.name()).color(NamedTextColor.GREEN))
                                    .collect(Collectors.toList()))).colorIfAbsent(NamedTextColor.GRAY));
            // "Disabled: A, B, C"
            msg(Component.translatable("w2k.command.experiments.disabled_list",
                    Component.join(JoinConfiguration.commas(true),
                            Arrays.stream(Experiment.values()).filter(ex -> !enabled.contains(ex))
                                    .map(experiment -> Component.text(experiment.name()).color(NamedTextColor.RED))
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

    @ExecutionPath("list")
    public void listAll()
    {
        // All Experiments:
        msg(Component.translatable("w2k.command.experiments.all_experiments").decorate(TextDecoration.BOLD));

        // A, B, C
        msg(Component.join(JoinConfiguration.commas(true), Arrays.stream(Experiment.values())
                .map(experiment -> Component.text(experiment.name()).color(ExperimentManager.isExperimentEnabled(experiment)
                                ? NamedTextColor.GREEN : NamedTextColor.RED).hoverEvent(HoverEvent.showText(
                                Component.translatable("w2k.command.experiments.click_for_more_information")))
                        .clickEvent(ClickEvent.runCommand("/experiments details " + experiment.name())))
                .collect(Collectors.toList())).colorIfAbsent(NamedTextColor.GRAY));
    }

    @ExecutionPath("details")
    public void redirectSummary()
    {
        summary();
    }

    @ExecutionPath("details")
    public void experimentDetails(final @Argument(label = "experiment", resolver = "w2k:experiment") Experiment experiment)
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

    @ExecutionPath("set")
    public void setExperimentState(final @Argument(label = "experiment", resolver = "w2k:experiment") Experiment experiment,
                                   final @Argument(label = "value", resolver = "brigadier:bool") boolean enabled)
    {
        if (experiment.isParameterOnly())
        {
            msg(Component.translatable("w2k.command.experiments.parameter_only", NamedTextColor.RED));
            return;
        }

        if (ExperimentManager.isExperimentEnabled(experiment) == enabled)
        {
            msg(Component.translatable("w2k.command.experiments.already_" + (enabled ? "enabled" : "disabled")));
            return;
        }

        if (!experiment.isAvailable())
        {
            msg(Component.translatable("w2k.command.experiments.cannot_be_enabled"));
            experiment.getFailedConditions().forEach(condition -> msg(Component.text(" - ", NamedTextColor.RED)
                    .append(Component.text(condition.getLabel(), NamedTextColor.YELLOW))));
            return;
        }

        if (enabled)
        {
            ExperimentManager.enableExperiment(experiment);
        }
        else
        {
            ExperimentManager.disableExperiment(experiment);
        }

        msg(Component.translatable("w2k.command.experiments.experiment_" + (enabled ? "enabled" : "disabled"),
                        Component.text(experiment.name()).color(NamedTextColor.DARK_GREEN))
                .color(NamedTextColor.GREEN));
    }

    @Override
    public boolean executeCommand(String commandLabel, String[] args)
    {
        if (args.length == 0 || args.length == 1 && args[0].equalsIgnoreCase("details"))
        {
            summary();
            return false;
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("list"))
        {
            listAll();
            return true;
        }
        else if (args.length == 2)
        {
            final Optional<Experiment> optional = Experiment.findExperiment(args[1]);

            if (optional.isPresent())
            {
                Experiment experiment = optional.get();

                switch (args[0].toLowerCase())
                {
                    case "details":
                    {
                        experimentDetails(experiment);
                        break;
                    }
                    case "enable":
                    {
                        setExperimentState(experiment, true);
                        break;
                    }
                    case "disable":
                    {
                        setExperimentState(experiment, false);
                        break;
                    }
                    default:
                    {
                        return false;
                    }
                }
            }
            else
            {
                msg(Component.translatable("w2k.command.experiments.invalid_experiment"));
            }

            return true;
        }
        else
        {
            return false;
        }
    }
}
