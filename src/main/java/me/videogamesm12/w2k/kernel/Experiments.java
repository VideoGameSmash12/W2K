package me.videogamesm12.w2k.kernel;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Experiments
{
    COMMAND_LINE_LAF_OVERRIDE,
    DEFAULT_LAF_BASED_ON_SYSTEM_CONFIG,
    DRASTIC_NON_PLAYER_ENTITY_REMOVAL_OPTION,
    EXPERIMENTS_EXISTENCE_ACKNOWLEDGED;

    private static List<Experiments> enabled = null;

    public Component getName()
    {
        return Component.translatable("w2k.experiment." + name().toLowerCase() + ".name");
    }

    public Component getDescription()
    {
        return Component.translatable("w2k.experiment." + name().toLowerCase() + ".description");
    }

    public static boolean experimentEnabled(Experiments experiment)
    {
        return getEnabledExperiments().contains(experiment);
    }

    public static List<Experiments> getEnabledExperiments()
    {
        if (enabled == null)
        {
            enabled = Arrays.stream(System.getProperty("me.videogamesm12.w2k.enabled_experiments", "").split(","))
                    .filter(enabled -> Arrays.stream(values()).anyMatch(ex -> enabled.equalsIgnoreCase(ex.name())))
                    .map(ex -> Arrays.stream(values()).filter(enabled -> enabled.name().equalsIgnoreCase(ex)).findFirst()
                            .orElseThrow(() -> new IllegalStateException("Ghost experiment"))).collect(Collectors.toList());
        }

        return enabled;
    }
}
