package me.videogamesm12.w2k.blackbox;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.videogamesm12.w2k.blackbox.command.BlackboxCmd;
import me.videogamesm12.w2k.blackbox.window.tool.crashpad.Crashpad;
import me.videogamesm12.w2k.kernel.Experiments;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.kernel.util.SysUtils;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.blackbox.theming.ThemeRegistry;
import me.videogamesm12.w2k.blackbox.theming.inbuilt.IBThemes;
import me.videogamesm12.w2k.blackbox.window.GUI;
import me.videogamesm12.w2k.blackbox.window.SysTray;
import me.videogamesm12.w2k.supervisor.api.event.ClientFreezeEvent;
import net.fabricmc.loader.api.FabricLoader;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class Blackbox extends Thread
{
    @Getter
    private static Blackbox instance;
    //--
    public static void setup()
    {
        System.setProperty("java.awt.headless", "false");
        //--
        instance = new Blackbox();
        instance.start();
    }

    public static File getFolder()
    {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), "w2k-blackbox");
    }

    @Getter
    private Configuration config;
    @Getter
    private GUI mainWindow;
    @Getter
    private SysTray systemTrayIcon;
    @Getter
    private boolean freezesIgnored;

    @Override
    public void run()
    {
        W2K.getEventBus().register(this);
        Supervisor.getEventBus().register(this);

        config = Configuration.load();

        ThemeRegistry.setupThemes();
        try
        {
            if (Experiments.experimentEnabled(Experiments.COMMAND_LINE_LAF_OVERRIDE)
                    && System.getProperty("me.videogamesm12.w2k.blackbox_theme") != null)
            {
                config.setTheme(System.getProperty("me.videogamesm12.w2k.blackbox_theme"));
            }

            ThemeRegistry.getTheme(config.getTheme()).apply();
        }
        catch (Exception ex)
        {
            W2K.getLogger().error("Failed to apply selected theme", ex);
            ThemeRegistry.getTheme(IBThemes.METAL.getInternalName()).apply();
        }

        // Non-Linux operating systems open the window and set up the system tray icons earlier than Linux operating systems do.
        // If it wasn't like this, issues like this would happen:  https://github.com/VideoGameSmash12/WNT/issues/11
        if (SysUtils.getOperatingSystem() != SysUtils.OperatingSystem.LINUX)
        {
            startup();
        }

        W2K.getInstance().getCommandManager().registerCommand(BlackboxCmd.class);
    }
    
    @Subscribe
    public void onClientStarted(ClientStartedEvent event)
    {
        // Linux operating systems open the window and set up the system tray icons later than non-Linux operating systems do.
        // If it wasn't like this, issues like this would happen:  https://github.com/VideoGameSmash12/WNT/issues/11
        if (SysUtils.getOperatingSystem() == SysUtils.OperatingSystem.LINUX)
        {
            startup();
        }
    }

    @Subscribe
    public void onClientStopped(ClientStoppedEvent event)
    {
        Configuration.save(config);
    }

    @Subscribe
    public void onClientFreeze(ClientFreezeEvent event)
    {
        if (config.isIgnoringFreezesDuringStartup() && !Supervisor.getInstance().getFlags().isGameStartedYet() ||
                mainWindow != null && mainWindow.isVisible() || freezesIgnored)
        {
            return;
        }

        int response = JOptionPane.showConfirmDialog(Blackbox.getInstance().getMainWindow(),
                "Your client froze. Would you like to open the Blackbox?", "Yikes!",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION)
        {
            openWindow();
        }
        else
        {
            freezesIgnored = true;
        }
    }

    @Subscribe
    public void onClientCrashed(ClientCrashedEvent event)
    {
        int response = JOptionPane.showConfirmDialog(Blackbox.getInstance().getMainWindow(),
                "Your client crashed. Would you like to view the crash report?", "Uh oh!",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION)
        {
            switch (SysUtils.getOperatingSystem())
            {
                default:
                case LINUX:
                {
                    final Crashpad crashpad = new Crashpad(event.getCrashReportFile());
                    final AtomicBoolean done = new AtomicBoolean(false);
                    crashpad.setVisible(true);
                    crashpad.setIconImage(Blackbox.getInstance().getMainWindow() != null ?
                            Blackbox.getInstance().getMainWindow().getIconImage() : null);

                    // Awful hacks below
                    crashpad.addWindowListener(new WindowAdapter()
                    {
                        @Override
                        public void windowClosed(WindowEvent e)
                        {
                            super.windowClosed(e);
                            done.set(true);
                        }
                    });
                    while (true)
                    {
                        if (done.get() || !crashpad.isVisible())
                        {
                            break;
                        }

                        continue;
                    }

                    break;
                }
                case WINDOWS:
                {
                    try
                    {
                        SysUtils.execute("notepad", event.getCrashReportFile().getAbsolutePath());
                    }
                    catch (Throwable ex)
                    {
                        JOptionPane.showMessageDialog(Blackbox.getInstance().getMainWindow(),
                                "We weren't able to open Notepad. The crash report is located at "
                                        + event.getCrashReportFile().getAbsolutePath() + ".");
                    }

                    break;
                }
            }
        }

        event.setCancelled(true);
    }

    private void startup()
    {
        if (config.isSystemTrayEnabled())
        {
            setupSystemTrayIcon();
        }

        if (config.isShowOnStartupEnabled() && mainWindow == null)
        {
            openWindow();
        }
    }

    public void openWindow()
    {
        if (mainWindow == null)
        {
            mainWindow = new GUI();
        }

        mainWindow.setVisible(true);
    }

    public void setupSystemTrayIcon()
    {
        W2K.getLogger().info("Setting up system tray integration...");

        if (systemTrayIcon == null)
        {
            systemTrayIcon = new SysTray(this);

            try
            {
                systemTrayIcon.addIcon();
            }
            catch (Exception ex)
            {
                W2K.getLogger().warn("Failed to set up system tray integration", ex);
            }
        }
    }
}
