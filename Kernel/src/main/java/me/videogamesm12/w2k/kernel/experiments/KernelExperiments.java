package me.videogamesm12.w2k.kernel.experiments;

import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentSupplier;

public class KernelExperiments extends ExperimentSupplier
{
    public static final Experiment PANIC_KEY_COMBINATION = new Experiment(
            "w2k-kernel:panic_key_combination",
            "w2k.kernel.experiment.panic_key_combination.name",
            "w2k.kernel.experiment.panic_key_combination.description",
            false);

    public KernelExperiments()
    {
        register(PANIC_KEY_COMBINATION);
    }
}
