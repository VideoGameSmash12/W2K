package me.videogamesm12.w2k.kernel.experiments;

import me.videogamesm12.w2k.kernel.experiment.Condition;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentSupplier;
import me.videogamesm12.w2k.kernel.util.VersionUtils;

import java.util.Collections;

public class KernelExperiments extends ExperimentSupplier
{
    public static final Experiment COMMAND_SYSTEM_OVERHAUL = new Experiment(
            "w2k-kernel:command_system_overhaul",
            "w2k.kernel.experiment.command_system_overhaul.name",
            "w2k.kernel.experiment.command_system_overhaul.description",
            true,
            Collections.singletonList(Condition.of("Only available for 1.20.1", VersionUtils.isRunning("1.20.1"))));
    public static final Experiment PANIC_KEY_COMBINATION = new Experiment(
            "w2k-kernel:panic_key_combination",
            "w2k.kernel.experiment.panic_key_combination.name",
            "w2k.kernel.experiment.panic_key_combination.description",
            false);

    public KernelExperiments()
    {
        register(COMMAND_SYSTEM_OVERHAUL);
        register(PANIC_KEY_COMBINATION);
    }
}
