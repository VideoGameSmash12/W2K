package me.videogamesm12.w2k.blackbox.window.menu.w2k;

import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;

import javax.swing.*;

public class ExperimentsMenu extends JMenu
{
    public ExperimentsMenu()
    {
        super("Experiments");

        ExperimentManager.getRegisteredExperiments().forEach(experiment ->
        {
            final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(experiment.getIdentifier() + " " + (experiment.isAvailable() ?
                    (experiment.isParameterOnly() ? "(Can't be toggled)" : "") : "(Unavailable)"));
            menuItem.setEnabled(experiment.isAvailable() && !experiment.isParameterOnly());
            menuItem.addActionListener((e) ->
            {
                if (ExperimentManager.isExperimentEnabled(experiment))
                {
                    ExperimentManager.disableExperiment(experiment);
                }
                else
                {
                    ExperimentManager.enableExperiment(experiment);
                }
            });
            menuItem.setSelected(ExperimentManager.isExperimentEnabled(experiment));
            add(menuItem);
        });
    }
}
