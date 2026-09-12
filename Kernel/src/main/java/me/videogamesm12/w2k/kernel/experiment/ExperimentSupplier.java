package me.videogamesm12.w2k.kernel.experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class ExperimentSupplier implements Supplier<List<Experiment>>
{
    private final List<Experiment> experiments = new ArrayList<>();

    public void register(Experiment experiment)
    {
        this.experiments.add(experiment);
    }

    @Override
    public List<Experiment> get()
    {
        return experiments;
    }
}
