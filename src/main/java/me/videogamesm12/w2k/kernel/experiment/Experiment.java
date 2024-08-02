package me.videogamesm12.w2k.kernel.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@RequiredArgsConstructor
public enum Experiment
{
    BLACKBOX_HELP_WINDOW("Blackbox", true, Collections.EMPTY_LIST),
    BLACKBOX_COMMAND_LINE_LAF_OVERRIDE("Blackbox", true, Collections.EMPTY_LIST),
    BLACKBOX_DEFAULT_LAF_BASED_ON_SYSTEM_CONFIG("Blackbox", true, Collections.EMPTY_LIST),
    BLACKBOX_RUNTIME_PROPERTIES_TAB("Blackbox", true, Collections.EMPTY_LIST),
    KERNEL_APPEND_DETAILS_TO_CRASH_REPORTS("Kernel", false, Collections.EMPTY_LIST),
    SUPERVISOR_CATCHES_CRASHES("Supervisor", false, Collections.EMPTY_LIST),
    INTEGRATOR_MOD_ICONS("Integrator", true, Collections.EMPTY_LIST),
    INTEGRATOR_WURST_ALT_MANAGER("Integrator", true, Collections.singletonList(Condition.of("Requires Wurst",
            FabricLoader.getInstance().isModLoaded("wurst"))));

    private final String mod;
    private boolean parameterOnly;
    private List<Condition> availability;

    public Component getName()
    {
        return Component.translatable("w2k.experiment." + name().toLowerCase() + ".name");
    }

    public Component getDescription()
    {
        return Component.translatable("w2k.experiment." + name().toLowerCase() + ".description");
    }

    public List<Condition> getFailedConditions()
    {
        return availability.stream().filter(condition -> !condition.conditionMet()).collect(Collectors.toList());
    }

    public boolean isAvailable()
    {
        return availability.isEmpty() || availability.stream().allMatch(Condition::conditionMet);
    }

    public static Optional<Experiment> findExperiment(String label)
    {
        return Arrays.stream(values()).filter(entry -> entry.name().equalsIgnoreCase(label)).findAny();
    }
}
