package me.videogamesm12.w2k.kernel.experiment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExperimentManager
{
    private static final Map<Experiments, Boolean> enabledExperimentMap = new HashMap<>();

    static
    {
        // Read from the command line parameters.
        // In the future, experiments will optionally include "requirements" to allow for experiments to only be enabled
        //  under select conditions (such as the user's operating system, game version, external mod configuration, etc.)
        //  but we'll deal with that when the need arises.
        final String[] enabled = System.getProperty("me.videogamesm12.w2k.enabled_experiments", "").split(",");
        Arrays.stream(Experiments.values()).forEach(entry -> enabledExperimentMap.put(entry, Arrays.stream(enabled)
                .anyMatch(requested -> requested.equalsIgnoreCase(entry.name()))));
    }

    public static boolean isExperimentEnabled(Experiments experiment)
    {
        return enabledExperimentMap.get(experiment);
    }

    public static List<Experiments> getEnabledExperiments()
    {
        return enabledExperimentMap.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static boolean enableExperiment(Experiments experiment)
    {
        if (experiment.isParameterOnly())
        {
            return false;
        }

        enabledExperimentMap.put(experiment, true);
        return true;
    }

    public static boolean disableExperiment(Experiments experiment)
    {
        if (experiment.isParameterOnly())
        {
            return false;
        }

        enabledExperimentMap.put(experiment, false);
        return true;
    }
}
