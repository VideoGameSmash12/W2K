package me.videogamesm12.w2k.blackbox.window.tool.console;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.api.event.ChatMessageEvent;

import javax.swing.*;
import java.awt.*;

public class Console extends JFrame
{
    private final JTabbedPane tabs;

    public Console()
    {
        super("Blackbox Console");
        setName("Blackbox Console");
        setMinimumSize(new Dimension(640, 360));
        setPreferredSize(new Dimension(640, 400));

        // Set up the tabs
        this.tabs = new JTabbedPane();
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(tabs,
                GroupLayout.Alignment.TRAILING));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(tabs,
                GroupLayout.Alignment.TRAILING));
        addTab(new MainChatTab());
        addTab(new FantasiaTab());

        // Set up the menu bar
        final JMenuBar menuBar = new JMenuBar();

        // OUTPUT
        final JMenu output = new JMenu("Output");
        final JMenuItem clearOutput = new JMenuItem("Clear Output");
        clearOutput.addActionListener((e) -> {
            if (tabs.getSelectedComponent() instanceof AbstractTab)
            {
                AbstractTab tab = (AbstractTab) tabs.getSelectedComponent();
                tab.clear();
            }
        });
        output.add(clearOutput);
        menuBar.add(output);

        setJMenuBar(menuBar);

        // Finally, we do this
        pack();

        // Register event listeners
        Supervisor.getEventBus().register(this);
    }

    @Subscribe
    public void onChatMessage(ChatMessageEvent event)
    {
        // Future-proofing; I might add integration for AdvancedChat in the future so why not lay the groundwork for that?
        for (int i = 0; i < tabs.getTabCount(); i++)
        {
            if (tabs.getComponentAt(i) instanceof AbstractTab)
            {
                AbstractTab tab = (AbstractTab) tabs.getComponentAt(i);
                tab.showMessage(event.getMessage());
            }
        }
    }

    public void addTab(AbstractTab tab)
    {
        tabs.addTab(tab.name(), tab);
    }
}
