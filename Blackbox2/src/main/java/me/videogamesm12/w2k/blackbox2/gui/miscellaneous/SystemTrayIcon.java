package me.videogamesm12.w2k.blackbox2.gui.miscellaneous;

import com.google.common.base.Preconditions;
import me.videogamesm12.w2k.blackbox2.Blackbox;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SystemTrayIcon
{
    private final TrayIcon icon;

    public SystemTrayIcon()
    {
        Preconditions.checkState(isSupported(), "The system tray is not supported on this operating system");

        icon = new TrayIcon(Toolkit.getDefaultToolkit().createImage(
                Blackbox.class.getClassLoader().getResource("assets/w2k/blackbox/icons/default/icon.png")),
                "Click here to open the Blackbox");
        icon.setImageAutoSize(true);
        icon.addActionListener(e -> Blackbox.getInstance().openMainWindow());
        icon.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                Blackbox.getInstance().openMainWindow();
            }
        });

        add();
    }

    public void add()
    {
        try
        {
            SystemTray.getSystemTray().add(icon);
        }
        catch (AWTException ex)
        {
            ex.printStackTrace();
        }
    }

    public void dispose()
    {
        if (icon == null)
        {
            return;
        }

        SystemTray.getSystemTray().remove(icon);
    }

    public void setTooltip(final String tooltip)
    {
        if (icon == null)
        {
            return;
        }

        icon.setToolTip(tooltip);
    }

    public static boolean isSupported()
    {
        return SystemTray.isSupported();
    }
}
