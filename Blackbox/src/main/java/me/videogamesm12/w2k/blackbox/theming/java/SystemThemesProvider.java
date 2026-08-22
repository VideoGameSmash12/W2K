package me.videogamesm12.w2k.blackbox.theming.java;

import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeProvider;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class SystemThemesProvider implements IThemeProvider
{
    private static final String themePackage = "com.sun.java.swing.plaf";
    private static final Map<String, ITheme> themes = new HashMap<>();
    private static final IThemeType type = new IThemeType()
    {
        @Override
        public String getLabel()
        {
            return "Java | System Themes";
        }

        @Override
        public int getId()
        {
            return 0;
        }
    };

    static
    {
        discoverAvailableThemes().forEach(theme -> themes.put(theme.getInternalName(), theme));
    }

    @Override
    public Map<String, ITheme> getThemes()
    {
        return themes;
    }

    @Override
    public List<IThemeType> getTypes()
    {
        return Collections.singletonList(type);
    }

    private static List<SystemTheme> discoverAvailableThemes()
    {
        return Arrays.stream(UIManager.getInstalledLookAndFeels())
                .filter(laf -> laf.getClassName().startsWith(themePackage))
                .map(laf -> new SystemTheme(laf))
                .collect(Collectors.toList());
    }

    @RequiredArgsConstructor
    public static class SystemTheme implements ITheme
    {
        private final UIManager.LookAndFeelInfo info;

        @Override
        public String getInternalName()
        {
            return "java-system:" + info.getClassName();
        }

        @Override
        public String getThemeName()
        {
            return info.getName();
        }

        @Override
        public String getThemeDescription()
        {
            return "";
        }

        @Override
        public IThemeType getType()
        {
            return type;
        }

        @Override
        public String getThemeClass()
        {
            return info.getClassName();
        }

        @Override
        public boolean isSupposedToShow()
        {
            return true;
        }

        @Override
        public void apply()
        {
            try
            {
                UIManager.setLookAndFeel(info.getClassName());
            }
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                   UnsupportedLookAndFeelException ex)
            {
                ex.printStackTrace();
            }
        }

        @Override
        public void showOptionalMessage()
        {
        }
    }
}
