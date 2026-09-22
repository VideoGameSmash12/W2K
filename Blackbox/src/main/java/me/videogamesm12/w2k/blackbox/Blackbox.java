package me.videogamesm12.w2k.blackbox;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.videogamesm12.w2k.blackbox.command.BlackboxCmd;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.window.tool.crashpad.Bootstrap;
import me.videogamesm12.w2k.blackbox.window.tool.crashpad.Crashpad;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.miscellaneous.KeyPressEvent;
import me.videogamesm12.w2k.kernel.event.miscellaneous.PanicKeyCombinationEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.kernel.util.KeyboardUtils;
import me.videogamesm12.w2k.kernel.util.SysUtils;
import me.videogamesm12.w2k.kernel.util.VersionUtils;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.blackbox.theming.ThemeRegistry;
import me.videogamesm12.w2k.blackbox.window.GUI;
import me.videogamesm12.w2k.blackbox.window.SysTray;
import me.videogamesm12.w2k.supervisor.api.event.ClientFreezeEvent;
import net.fabricmc.loader.api.FabricLoader;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Blackbox extends Thread
{
    @Getter
    private static final EventBus eventBus = new EventBus();
    @Getter
    private static Blackbox instance;

    public Blackbox()
    {
        super("Blackbox");
    }

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
            if (System.getProperty("me.videogamesm12.w2k.blackbox_theme") != null)
            {
                config.setTheme(System.getProperty("me.videogamesm12.w2k.blackbox_theme"));
            }

            ThemeRegistry.getThemeSafe(config.getTheme()).ifPresent(ITheme::apply);
        }
        catch (Exception ex)
        {
            W2K.getLogger().error("Failed to apply selected theme", ex);
            try
            {
                UIManager.setLookAndFeel(new MetalLookAndFeel());
            }
            catch (UnsupportedLookAndFeelException ignored)
            {
            }
        }

        // Optional late start-up mode just in case something prevents the client from booting normally
        if (System.getProperty("me.videogamesm12.w2k.blackbox_late_start", "false").contains("f"))
        {
            startup();
        }

        W2K.getInstance().getCommandManager().registerCommand(BlackboxCmd.class);
    }
    
    @Subscribe
    public void onClientStarted(ClientStartedEvent event)
    {
        // Late startup mode - Forces the Blackbox to set itself up later on
        if (!System.getProperty("me.videogamesm12.w2k.blackbox_late_start", "false").contains("f"))
        {
            startup();
        }
    }

    @Subscribe
    public void onClientStopped(ClientStoppedEvent event)
    {
        // Set the width and height in the configuration so that the user's preferences are kept
        if (mainWindow != null)
        {
            config.setWidth(mainWindow.getWidth());
            config.setHeight(mainWindow.getHeight());
            mainWindow.getTimer().cancel();
            mainWindow.dispose();
        }

        // Remove the icon if it's present
        if (systemTrayIcon != null)
        {
            systemTrayIcon.removeIcon();
        }

        Configuration.save(config);
        super.interrupt();
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
                "Your client crashed. Would you like to view the crash report?", "Yikes!",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION)
        {
            switch (SysUtils.getOperatingSystem())
            {
                case WINDOWS:
                {
                    try
                    {
                        SysUtils.execute("notepad", event.getCrashReportFile().getAbsolutePath());
                        break;
                    }
                    catch (Throwable ignored)
                    {
                        W2K.getLogger().warn("Failed to open Notepad, falling back to Crashpad");
                    }
                }
                case LINUX:
                default:
                {
                    try
                    {
                        SysUtils.execute(Paths.get(System.getProperty("java.home"), "bin", "java").toString(),
                                "-cp",
                                Paths.get(Blackbox.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString(),
                                Bootstrap.class.getName(),
                                event.getCrashReportFile().getAbsolutePath()).waitFor();
                    }
                    catch (InterruptedException ignored)
                    {
                    }
                    catch (IOException | URISyntaxException ex)
                    {
                        W2K.getLogger().error("Couldn't launch Crashpad", ex);
                    }

                    break;
                }
            }
        }

        event.setCancelled(true);
    }

    @Subscribe
    public void onPanicKeyCombination(KeyPressEvent event)
    {
        // Microsoft, in their infinite "wisdom", replaced the context menu key with the stupid Copilot key because they
        //  were huffling glue trying to shove AI into absolutely everything they could. As such, some keyboards no
        //  longer have the context menu key, so we have to have a secondary combination as a backup.
        //
        // I chose CTRL + Context Menu for the primary combination and CTRL + ALT + Z for the backup combination.

        final Integer controlModifier = KeyboardUtils.getModifier("modifier.control");
        final Integer altModifier = KeyboardUtils.getModifier("modifier.alt");
        final Integer menuKey = KeyboardUtils.getKeyId("key.keyboard.menu");
        final Integer zKey = KeyboardUtils.getKeyId("key.keyboard.z");

        // Don't bother if our keys don't exist
        if (controlModifier == null || altModifier == null || menuKey == null || zKey == null)
        {
            return;
        }

        // Ctrl + Alt + Z or Ctrl + Menu opens the Blackbox
        if ((event.getModifiers() == controlModifier + altModifier && event.getKeyCode() == zKey)
                || event.getModifiers() == controlModifier && event.getKeyCode() == menuKey)
        {
            SwingUtilities.invokeLater(() -> Blackbox.getInstance().openWindow());
        }
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
        mainWindow.toFront();

        if (!ThemeRegistry.getThemeSafe(config.getTheme()).isPresent())
        {
            JOptionPane.showMessageDialog(mainWindow, "There was a problem with the theme you had set, so we have defaulted to a fallback. Please reset the theme in the settings.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
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
