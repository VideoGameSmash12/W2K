package me.videogamesm12.w2k.kernel.commands;

import me.videogamesm12.w2k.kernel.Experiments;
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
import java.util.stream.Collectors;

@Parameters(name = "experiments", usage = "/<command> [experiment]")
public class ExperimentsCmd extends WCommand
{
    @Override
    public boolean executeCommand(String commandLabel, String[] args)
    {
        if (args.length > 1)
        {
            return false;
        }
        else if (args.length == 1)
        {
            try
            {
                final Experiments experiment = Experiments.valueOf(args[0].toUpperCase());
                msg(Component.translatable("w2k.command.experiments.experiment_details").decorate(TextDecoration.BOLD));
                msg(Component.translatable("w2k.command.experiments.experiment_details_name", experiment.getName().color(NamedTextColor.WHITE)).colorIfAbsent(NamedTextColor.GRAY));
                msg(Component.translatable("w2k.command.experiments.experiment_details_description", experiment.getDescription().color(NamedTextColor.WHITE)).colorIfAbsent(NamedTextColor.GRAY));

            }
            catch (IllegalArgumentException ex)
            {
                msg(Component.translatable("w2k.command.experiments.invalid_experiment"));
            }
        }
        else
        {
            List<Experiments> enabled = Experiments.getEnabledExperiments();

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
                                    Component.text("https://github.com/VideoGameSmash12/W2K/wiki/Experiments")
                                            .color(NamedTextColor.BLUE)
                                            .decorate(TextDecoration.UNDERLINED)
                                            .clickEvent(ClickEvent.openUrl("https://github.com/VideoGameSmash12/W2K/wiki/Experiments")))
                            .colorIfAbsent(NamedTextColor.GRAY)))
                    .colorIfAbsent(NamedTextColor.BLUE).decorate(TextDecoration.UNDERLINED));
        }

        return true;
    }
}
