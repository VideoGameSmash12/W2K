package me.videogamesm12.w2k.blackbox.window.menu.settings;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.ThemeRegistry;
import me.videogamesm12.w2k.kernel.W2K;

import javax.swing.*;
import java.util.Comparator;

/**
 * <h1>ThemeMenu</h1>
 * A menu for the theme selection in the Blackbox.
 */
public class ThemeMenu extends JMenu
{
    private final ButtonGroup group = new ButtonGroup();

    public ThemeMenu()
    {
        super("Theme");

        ThemeRegistry.getThemeTypes().forEach(type ->
        {
            JMenu menu = new JMenu(type.getLabel());
            add(menu);

            if (ThemeRegistry.getThemes().entrySet().stream().noneMatch(theme -> theme.getValue().getType().getId() == type.getId() && theme.getValue().isSupposedToShow()))
            {
                JMenuItem emptyItem = new JMenuItem("(none)");
                emptyItem.setEnabled(false);
                menu.add(emptyItem);
                return;
            }

            ThemeRegistry.getThemes().entrySet().stream().filter(theme -> theme.getValue().getType().getId() == type.getId() && theme.getValue().isSupposedToShow()).sorted(Comparator.comparing(set -> set.getValue().getThemeName())).forEach(set ->
            {
                String themeId = set.getKey();
                ITheme theme = set.getValue();
                //--
                JRadioButtonMenuItem themeItem = new JRadioButtonMenuItem();
                themeItem.setText(theme.getThemeName());
                themeItem.setToolTipText(theme.getThemeDescription());
                themeItem.setSelected(Blackbox.getInstance().getConfig().getTheme().equalsIgnoreCase(themeId));
                themeItem.addActionListener((e) ->
                {
                    W2K.getLogger().info("Switching theme to {}...", theme.getThemeName());
                    //--
                    ITheme originalTheme = ThemeRegistry.getTheme(Blackbox.getInstance().getConfig().getTheme());
                    Blackbox.getInstance().getConfig().setTheme(themeId);
                    //--
                    theme.apply();
                    theme.getType().update();
                    //--
                    if (originalTheme != null && originalTheme.getType().getId() != theme.getType().getId())
                    {
                        JOptionPane.showMessageDialog(this, "Theme changed. If things look broken, you may need to restart your Minecraft client for the changes to take full effect.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                group.add(themeItem);
                menu.add(themeItem);
            });
        });
    }
}