package me.videogamesm12.w2k.blackbox2;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.videogamesm12.w2k.blackbox2.gui.main.MainWindow;
import me.videogamesm12.w2k.blackbox2.gui.miscellaneous.SystemTrayIcon;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.kernel.util.SysUtils;
import me.videogamesm12.w2k.supervisor.api.event.ClientFreezeEvent;

import javax.swing.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Blackbox
{
    @Getter
    private static Blackbox instance;
    //
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    //
    @Getter
    private MainWindow mainWindow;
    @Getter
    private SystemTrayIcon systemTrayIcon;

    public Blackbox()
    {
        Preconditions.checkState(instance == null, "An instance of the Blackbox has already been initialized");

        instance = this;

        W2K.getEventBus().register(this);
    }

    public void setup()
    {
        openMainWindow();
        addSystemTrayIcon();
    }

    public void shutdown()
    {
        executor.shutdownNow();

        if (mainWindow != null)
        {
            mainWindow.setVisible(false);
            mainWindow.dispose();
        }

        if (systemTrayIcon != null)
        {
            systemTrayIcon.dispose();
        }
    }

    public void openMainWindow()
    {
        run(() ->
        {
            if (mainWindow == null)
            {
                mainWindow = new MainWindow();
            }

            mainWindow.setVisible(true);
        });
    }

    public void addSystemTrayIcon()
    {
        run(() ->
        {
            if (SystemTrayIcon.isSupported())
            {
                systemTrayIcon = new SystemTrayIcon();
                systemTrayIcon.add();
            }
        });
    }

    public void queue(final Runnable operation)
    {
        Preconditions.checkNotNull(operation, "Operation cannot be null");
        executor.submit(operation);
    }

    public void run(final Runnable operation)
    {
        Preconditions.checkNotNull(operation, "Operation cannot be null");
        executor.execute(operation);
    }

    @Subscribe
    public void onClientShutdown(ClientStoppedEvent event)
    {
        // Save settings

        // Shutdown
        shutdown();
    }

    @Subscribe
    public void onClientCrash(ClientCrashedEvent event)
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
                    /*
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

                    break;*/
                }
            }
        }

        event.setCancelled(true);
    }

    @Subscribe
    public void onClientFreeze(ClientFreezeEvent event)
    {

    }
}
