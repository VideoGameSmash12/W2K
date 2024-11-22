package me.videogamesm12.w2k.blackbox.window;

import lombok.Getter;
import me.videogamesm12.w2k.blackbox.Blackbox;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SysTray
{
    @Getter
    private TrayIcon icon;

    public SysTray(Blackbox blackbox)
    {
        if (SystemTray.isSupported())
        {
            icon = new TrayIcon(Toolkit.getDefaultToolkit().createImage(
                    Blackbox.class.getClassLoader().getResource("assets/w2k-blackbox/icons/default/icon.png")),
                    "Blackbox - Click to Open");
            icon.setImageAutoSize(true);
            icon.addActionListener(e -> blackbox.openWindow());
            icon.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    blackbox.openWindow();
                }
            });
        }
    }

    public void addIcon() throws AWTException
    {
        SystemTray.getSystemTray().add(icon);
    }
}
