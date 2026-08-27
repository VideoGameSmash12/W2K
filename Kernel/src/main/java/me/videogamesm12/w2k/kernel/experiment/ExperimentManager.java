package me.videogamesm12.w2k.kernel.experiment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h1>ExperimentManager</h1>
 * <p>Utility class that manages the states of experiments to enable and which to disable.</p>
 */
public class ExperimentManager
{
    private static final Map<Experiment, Boolean> enabledExperimentMap = new HashMap<>();

    static
    {
        // Read from the command line parameters.
        final String[] enabled = System.getProperty("me.videogamesm12.w2k.enabled_experiments", "").split(",");
        Arrays.stream(Experiment.values()).forEach(entry -> enabledExperimentMap.put(entry, Arrays.stream(enabled)
                .anyMatch(requested -> requested.equalsIgnoreCase(entry.name())) && entry.isAvailable()));
    }

    /**
     * Returns whether the provided experiment is currently enabled
     * @param experiment    {@link me.videogamesm12.w2k.kernel.experiment.Experiment}
     * @return              True if the experiment is enabled
     */
    public static boolean isExperimentEnabled(Experiment experiment)
    {
        return enabledExperimentMap.get(experiment);
    }

    /**
     * Returns a list of experiments that are currently active and enabled
     * @return  {@code List<Experiment>}
     */
    public static List<Experiment> getEnabledExperiments()
    {
        return enabledExperimentMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Checks if an experiment can be enabled after runtime and if it is available and if both are the case, then flags
     *  the experiment as enabled.
     * @param experiment    {@link me.videogamesm12.w2k.kernel.experiment.Experiment}
     * @return              True if the experiment could be enabled
     */
    public static boolean enableExperiment(Experiment experiment)
    {
        if (experiment.isParameterOnly() || !experiment.isAvailable())
        {
            return false;
        }

        enabledExperimentMap.put(experiment, true);
        return true;
    }

    /**
     * Checks if an experiment can be disabled after runtime and if it is available and if both are the case, then flags
     *  the experiment as disabled
     * @param experiment    {@link me.videogamesm12.w2k.kernel.experiment.Experiment}
     * @return              True if the experiment could be disabled
     */
    public static boolean disableExperiment(Experiment experiment)
    {
        if (experiment.isParameterOnly() || !experiment.isAvailable())
        {
            return false;
        }

        enabledExperimentMap.put(experiment, false);
        return true;
    }
}
