package me.videogamesm12.w2k.kernel.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.util.SysUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <h1>Experiment</h1>
 * <p>An enum for every experiment in W2K.</p>
 * <p>Experiments can have a set of {@link me.videogamesm12.w2k.kernel.experiment.Condition}s that are checked on
 * runtime to determine which ones are available and can be enabled and which ones aren't.</p>
 * <p>To enable these, set the {@code me.videgamesm12.w2k.enabled_experiments} JVM argument in your instance to a
 * comma-separated list.</p>
 */
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
    INTEGRATOR_MOD_ICONS("Integrator", true, Collections.EMPTY_LIST),
    INTEGRATOR_WURST_ALT_MANAGER("Integrator", true, Collections.singletonList(Condition.of("Requires Wurst",
            FabricLoader.getInstance().isModLoaded("wurst"))));

    private final String mod;
    private boolean parameterOnly;
    private List<Condition> availability;

    /**
     * Returns a translatable component for the Experiment's user-facing name.
     * @return  {@link Component}
     */
    public Component getName()
    {
        return Component.translatable("w2k.experiment." + name().toLowerCase() + ".name");
    }

    /**
     * Returns a translatable component for the Experiment's user-facing description.
     * @return  {@link Component}
     */
    public Component getDescription()
    {
        return Component.translatable("w2k.experiment." + name().toLowerCase() + ".description");
    }

    /**
     * Returns a list of {@link Condition}s that weren't met.
     * @return  {@code List<Condition>}
     */
    public List<Condition> getFailedConditions()
    {
        return availability.stream().filter(condition -> !condition.conditionMet()).collect(Collectors.toList());
    }

    /**
     * Returns whether the conditions required for the experiment to be available have been met.
     * @return  True if there are no conditions present or if all of them have been met
     */
    public boolean isAvailable()
    {
        return availability.isEmpty() || availability.stream().allMatch(Condition::conditionMet);
    }

    /**
     * Get an optional {@link Experiment} with a provided String as the name.
     * @param label     String
     * @return          @{code Optional<Experiment>}
     */
    public static Optional<Experiment> findExperiment(String label)
    {
        return Arrays.stream(values()).filter(entry -> entry.name().equalsIgnoreCase(label)).findAny();
    }
}
