package me.videogamesm12.w2k.kernel.commands;

import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.experiment.Experiments;
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

@Parameters(name = "experiments", usage = "/<command> <<details | enable | disable> <experiment> | list>")
public class ExperimentsCmd extends WCommand
{
    @Override
    public boolean executeCommand(String commandLabel, String[] args)
    {
        if (args.length == 0 || args.length == 1 && args[0].equalsIgnoreCase("details"))
        {
            List<Experiments> enabled = ExperimentManager.getEnabledExperiments();

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
                                Arrays.stream(Experiments.values()).filter(ex -> !enabled.contains(ex))
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
                                    Component.text("/" + commandLabel + " enable/disable <experiment>")
                                            .clickEvent(ClickEvent.suggestCommand("/" + commandLabel + "enable "))
                                            .color(NamedTextColor.WHITE),
                                    Component.text("https://github.com/VideoGameSmash12/W2K/wiki/Experiments")
                                            .color(NamedTextColor.BLUE)
                                            .decorate(TextDecoration.UNDERLINED)
                                            .clickEvent(ClickEvent.openUrl("https://github.com/VideoGameSmash12/W2K/wiki/Experiments")))
                            .colorIfAbsent(NamedTextColor.GRAY)))
                    .colorIfAbsent(NamedTextColor.BLUE).decorate(TextDecoration.UNDERLINED));

            return false;
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("list"))
        {
            // All Experiments:
            msg(Component.translatable("w2k.command.experiments.all_experiments").decorate(TextDecoration.BOLD));

            // A, B, C
            msg(Component.join(JoinConfiguration.commas(true), Arrays.stream(Experiments.values())
                    .map(experiment -> Component.text(experiment.name()).color(ExperimentManager.isExperimentEnabled(experiment)
                            ? NamedTextColor.GREEN : NamedTextColor.RED).hoverEvent(HoverEvent.showText(
                                    Component.translatable("w2k.command.experiments.click_for_more_information")))
                            .clickEvent(ClickEvent.runCommand("/" + commandLabel + " details " + experiment.name())))
                    .collect(Collectors.toList())).colorIfAbsent(NamedTextColor.GRAY));

            return true;
        }
        else if (args.length == 2)
        {
            final Optional<Experiments> optional = Experiments.findExperiment(args[1]);

            if (optional.isPresent())
            {
                Experiments experiment = optional.get();

                switch (args[0].toLowerCase())
                {
                    case "details":
                    {
                        // Experiment Details:
                        msg(Component.translatable("w2k.command.experiments.experiment_details")
                                .decorate(TextDecoration.BOLD));
                        // Name: %s
                        msg(Component.translatable("w2k.command.experiments.experiment_details_name",
                                experiment.getName().color(NamedTextColor.WHITE)).colorIfAbsent(NamedTextColor.GRAY));
                        // Description: %s
                        msg(Component.translatable("w2k.command.experiments.experiment_details_description",
                                experiment.getDescription().color(NamedTextColor.WHITE)).colorIfAbsent(NamedTextColor.GRAY));
                        break;
                    }
                    case "enable":
                    {
                        if (ExperimentManager.isExperimentEnabled(experiment))
                        {
                            // That experiment is already enabled.
                            msg(Component.translatable("w2k.command.experiments.already_enabled"));
                            return true;
                        }

                        if (ExperimentManager.enableExperiment(experiment))
                        {
                            // The experiment %s has been enabled.
                            msg(Component.translatable("w2k.command.experiments.experiment_enabled",
                                    Component.text(experiment.name()).color(NamedTextColor.DARK_GREEN))
                                    .color(NamedTextColor.GREEN));
                        }
                        else
                        {
                            // This experiment can't be toggled during runtime. Its state must be manually set by
                            //  manually adding/removing it from your game's launch options.
                            msg(Component.translatable("w2k.command.experiments.parameter_only"));
                        }

                        break;
                    }
                    case "disable":
                    {
                        if (!ExperimentManager.isExperimentEnabled(experiment))
                        {
                            // That experiment is already disabled.
                            msg(Component.translatable("w2k.command.experiments.already_disabled"));
                            return true;
                        }

                        if (ExperimentManager.disableExperiment(experiment))
                        {
                            // The experiment %s has been disabled.
                            msg(Component.translatable("w2k.command.experiments.experiment_disabled",
                                            Component.text(experiment.name()).color(NamedTextColor.DARK_GREEN))
                                    .color(NamedTextColor.GREEN));
                        }
                        else
                        {
                            // This experiment can't be toggled during runtime. Its state must be manually set by
                            //  manually adding/removing it from your game's launch options.
                            msg(Component.translatable("w2k.command.experiments.parameter_only"));
                        }

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
