package me.videogamesm12.w2k.blackbox;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.kernel.util.SysUtils;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.blackbox.theming.ThemeRegistry;
import me.videogamesm12.w2k.blackbox.theming.inbuilt.IBThemes;
import me.videogamesm12.w2k.blackbox.window.GUI;
import me.videogamesm12.w2k.blackbox.window.SysTray;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

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

    @Override
    public void run()
    {
        W2K.getEventBus().register(this);
        Supervisor.getEventBus().register(this);

        config = Configuration.load();

        ThemeRegistry.setupThemes();
        try
        {
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

    private void startup()
    {
        setupSystemTrayIcon();

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
