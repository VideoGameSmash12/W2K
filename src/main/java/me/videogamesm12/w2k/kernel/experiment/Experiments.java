package me.videogamesm12.w2k.kernel.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
@RequiredArgsConstructor
public enum Experiments
{
    BLACKBOX_HELP_WINDOW("Blackbox", true),
    BLACKBOX_COMMAND_LINE_LAF_OVERRIDE("Blackbox", true),
    BLACKBOX_DEFAULT_LAF_BASED_ON_SYSTEM_CONFIG("Blackbox", true),
    SUPERVISOR_CATCHES_CRASHES("Supervisor", false),
    INTEGRATOR_MOD_ICONS("Integrator", true),
    INTEGRATOR_WURST_ALT_MANAGER("Integrator", true);

    private final String mod;
    private boolean parameterOnly;

    public Component getName()
    {
        return Component.translatable("w2k.experiment." + name().toLowerCase() + ".name");
    }

    public Component getDescription()
    {
        return Component.translatable("w2k.experiment." + name().toLowerCase() + ".description");
    }

    public static Optional<Experiments> findExperiment(String label)
    {
        return Arrays.stream(values()).filter(entry -> entry.name().equalsIgnoreCase(label)).findAny();
    }
}
