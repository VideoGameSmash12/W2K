package me.videogamesm12.w2k.blackbox.window.menu;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.Configuration;
import me.videogamesm12.w2k.blackbox.util.JComponents;
import me.videogamesm12.w2k.blackbox.window.menu.settings.ThemeMenu;
import me.videogamesm12.w2k.blackbox.window.tab.PlayersTab;

import javax.swing.*;

public class SettingsMenu extends JMenu
{
    public SettingsMenu()
    {
        super("Settings");

        final Configuration config = Blackbox.getInstance().getConfig();

        add(new ThemeMenu());
        //--
        addSeparator();
        //--
        add(JComponents.createCheckboxMenuItem("Auto-refresh",
                "Automatically updates the contents of the currently selected tab.",
                config::isAutoRefreshEnabled,
                config::setAutoRefreshEnabled));
        add(JComponents.createCheckboxMenuItem("Show on startup",
                "Automatically shows the Blackbox window when you start your client.",
                config::isShowOnStartupEnabled,
                config::setShowOnStartupEnabled));
        add(JComponents.createCheckboxMenuItem("Add icon to system tray",
                "Adds an icon to the system tray in your taskbar which opens the Blackbox upon clicking it.\n" +
                        "Some operating systems don't take kindly to a system tray, and on Linux it can be buggy.",
                config::isSystemTrayEnabled,
                config::setSystemTrayEnabled));
        add(JComponents.createCheckboxMenuItem("Ignore client freezes on startup",
                "Prevents the Supervisor from falsely flagging client freezes while the game is starting up.",
                config::isIgnoringFreezesDuringStartup,
                config::setIgnoringFreezesDuringStartup));
        add(JComponents.createCheckboxMenuItem("More detailed tabs",
                "Provides more information in certain tabs than by default.",
                config::isEnhancedListingEnabled,
                value ->
                {
                    config.setEnhancedListingEnabled(value);
                    Blackbox.getInstance().getMainWindow().setupTabs();
                }));
    }
}
