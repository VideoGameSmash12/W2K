package me.videogamesm12.w2k.blackbox.window;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.blackbox.window.menu.MitigationsMenu;
import me.videogamesm12.w2k.blackbox.window.menu.SettingsMenu;
import me.videogamesm12.w2k.blackbox.window.menu.ToolsMenu;
import me.videogamesm12.w2k.blackbox.window.tab.*;
import me.videogamesm12.w2k.blackbox.window.tool.console.Console;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ConcurrentModificationException;
import java.util.Timer;
import java.util.TimerTask;

public class GUI extends JFrame
{
    @Getter
    private Console console;
    //--
    private final JMenuBar menuBar;
    private final JTabbedPane tabbedPane;
    //--
    private final Timer timer;

    public GUI()
    {
        // Window basics
        setTitle("Blackbox");
        setName("Blackbox");
        setupIcon();

        // Sets up the window dimensions
        setMinimumSize(new Dimension(420, 560));
        setPreferredSize(new Dimension(Blackbox.getInstance().getConfig().getWidth(), Blackbox.getInstance().getConfig().getHeight()));

        // Sets up the components
        menuBar = new JMenuBar();
        menuBar.add(new W2KMenu());
        menuBar.add(new MitigationsMenu());
        menuBar.add(new SettingsMenu());
        menuBar.add(new ToolsMenu());
        add(menuBar);
        setJMenuBar(menuBar);
        //--
        tabbedPane = new JTabbedPane();
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(tabbedPane,
                GroupLayout.Alignment.TRAILING));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(tabbedPane,
                GroupLayout.Alignment.TRAILING));
        // TODO: Consider adding icons for each tab. It might make the UI look fancier.
        tabbedPane.addTab("General", new MainTab());
        tabbedPane.addTab("Players", new PlayersTab());
        tabbedPane.addTab("Entities", new EntitiesTab());
        // End of component setup

        // Sets up timers
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                // If the window is not visible, then don't do anything
                if (!isVisible())
                {
                    return;
                }

                // Automatically updates menus if they can be updated
                for (int i = 0; i < menuBar.getMenuCount(); i++)
                {
                    JMenu menu = menuBar.getMenu(i);

                    if (menu instanceof Dynamic)
                    {
                        Dynamic dynamic = (Dynamic) menu;

                        dynamic.update();
                    }
                }

                // Automatically "refreshes" the current tab if enabled and if it can even be updated
                if (Blackbox.getInstance().getConfig().isAutoRefreshEnabled()
                        && tabbedPane.getSelectedComponent() instanceof Dynamic)
                {
                    try
                    {
                        Dynamic dynamicTab = (Dynamic) tabbedPane.getSelectedComponent();
                        dynamicTab.update();
                    }
                    // As bad of a practice as this is, this is a necessary evil sometimes since we're accessing data
                    //  from another thread and this is by design
                    catch (ConcurrentModificationException ignored)
                    {
                    }
                    catch (Throwable ex)
                    {
                        W2K.getLogger().error("Couldn't update current tab", ex);
                    }
                }
            }
        }, 0, 1000);

        // Finally, we show the stuff now.
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);
        pack();
    }

    public void openConsoleWindow()
    {
        if (console == null)
        {
            console = new Console();
        }

        console.setVisible(true);
    }

    public void setupIcon()
    {
        try
        {
            // Loads the icon from disk.
            InputStream iconStream = Blackbox.class.getClassLoader().getResourceAsStream("assets/w2k-blackbox/icon.png");
            setIconImage(ImageIO.read(iconStream));
        }
        catch (Exception ex)
        {
            W2K.getLogger().error("Failed to load icon image", ex);
        }
    }
}
