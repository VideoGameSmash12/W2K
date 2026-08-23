package me.videogamesm12.w2k.blackbox.window.menu;

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
        final JMenuItem chatWindow = new JMenuItem("Open Console");
        chatWindow.setToolTipText("Opens the Blackbox Console.");
        chatWindow.addActionListener((e) -> {
            try
            {
                Blackbox.getInstance().getMainWindow().openConsoleWindow();
            }
            catch (Throwable ex)
            {
                ex.printStackTrace();
            }
        });
        add(chatWindow);
        //--
        final JMenuItem dumpThreads = new JMenuItem("Dump thread information");
        dumpThreads.setToolTipText("Dumps current thread information to disk.");
        dumpThreads.addActionListener((e) -> Supervisor.getInstance().dumpThreads().forEach(line -> W2K.getLogger().info(line)));
        add(dumpThreads);
        //--
        if (ExperimentManager.isExperimentEnabled(Experiment.BLACKBOX_HELP_WINDOW))
        {
            addSeparator();
            final JMenuItem helpWindow = new JMenuItem("Help");
            helpWindow.setToolTipText("Opens a help menu to guide you through the Blackbox.");
            helpWindow.addActionListener((e) -> Blackbox.getInstance().getMainWindow().openHelperWindow());
            add(helpWindow);
        }
    }
}
