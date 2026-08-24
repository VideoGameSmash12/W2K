package me.videogamesm12.w2k.blackbox.window.menu;

import me.videogamesm12.w2k.blackbox.util.JComponents;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.supervisor.Supervisor;

import javax.swing.*;

public class ToolsMenu extends JMenu
{
    public ToolsMenu()
    {
        super("Tools");
        //--
        add(JComponents.createMenuItem("Open Console",
                "Opens the Blackbox Console.",
                () ->
                {
                    try
                    {
                        Blackbox.getInstance().getMainWindow().openConsoleWindow();
                    }
                    catch (Throwable ex)
                    {
                        W2K.getLogger().error("Failed to open the Blackbox Console", ex);
                    }
                }));
        add(JComponents.createMenuItem("Dump thread information",
                "Dumps information about all threads in the client process to your latest.log file.",
                () -> Supervisor.getInstance().dumpThreads().forEach(line -> W2K.getLogger().info(line))));
        //--
        if (ExperimentManager.isExperimentEnabled(Experiment.BLACKBOX_HELP_WINDOW))
        {
            addSeparator();
            add(JComponents.createMenuItem("Help",
                    "Opens a help window containing information about W2K.",
                    () -> Blackbox.getInstance().getMainWindow().openHelperWindow()));
        }
    }
}
