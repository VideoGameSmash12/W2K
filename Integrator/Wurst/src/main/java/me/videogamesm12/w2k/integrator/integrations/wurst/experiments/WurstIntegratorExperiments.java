package me.videogamesm12.w2k.integrator.integrations.wurst.experiments;

import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentSupplier;

public class WurstIntegratorExperiments extends ExperimentSupplier
{
    public static final Experiment ALT_MANAGER = new Experiment("w2k-integrator-wurst:alt_manager",
            "w2k.integrator.wurst.experiment.alt_manager.name",
            "w2k.integrator.wurst.experiment.alt_manager.description",
            true);

    public WurstIntegratorExperiments()
    {
        register(ALT_MANAGER);
    }
}
