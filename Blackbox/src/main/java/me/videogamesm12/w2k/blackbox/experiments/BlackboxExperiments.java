package me.videogamesm12.w2k.blackbox.experiments;

import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentSupplier;

public class BlackboxExperiments extends ExperimentSupplier
{
    public static final Experiment HELP_WINDOW = new Experiment(
            "w2k-blackbox:help_window",
            "w2k.blackbox.experiment.help_window.name",
            "w2k.blackbox.experiment.help_window.description",
            true);
    public static final Experiment PROPERTIES_TAB = new Experiment(
            "w2k-blackbox:properties_tab",
            "w2k.blackbox.experiment.properties_tab.name",
            "w2k.blackbox.experiment.properties_tab.description",
            true);

    public BlackboxExperiments()
    {
        register(HELP_WINDOW);
        register(PROPERTIES_TAB);
    }
}
