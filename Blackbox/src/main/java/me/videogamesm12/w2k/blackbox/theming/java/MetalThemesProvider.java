package me.videogamesm12.w2k.blackbox.theming.java;

import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeProvider;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;

import javax.swing.*;
import javax.swing.plaf.metal.*;
import java.util.*;

public class MetalThemesProvider implements IThemeProvider
{
    private static final Map<String, ITheme> themes = new HashMap<>();
    private static final IThemeType type = new IThemeType()
    {
        @Override
        public String getLabel()
        {
            return "Java | Metal Themes";
        }

        @Override
        public int getId()
        {
            return 1;
        }
    };

    static
    {
        Arrays.stream(W2KMetalTheme.values()).forEach(theme -> themes.put(theme.getInternalName(), theme));
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

    @RequiredArgsConstructor
    public enum W2KMetalTheme implements ITheme
    {
        OCEAN("Ocean", new OceanTheme()),
        STEEL("Steel", new DefaultMetalTheme());

        private final String name;
        private final MetalTheme theme;

        @Override
        public String getInternalName()
        {
            return "java-metal:" + theme.getClass().getName();
        }

        @Override
        public String getThemeName()
        {
            return name;
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
            return null;
        }

        @Override
        public boolean isSupposedToShow()
        {
            return true;
        }

        @Override
        public void apply()
        {
            MetalLookAndFeel.setCurrentTheme(theme);
            try
            {
                UIManager.setLookAndFeel(new MetalLookAndFeel());
            }
            catch (UnsupportedLookAndFeelException ignored)
            {
            }
        }

        @Override
        public void showOptionalMessage()
        {
        }
    }
}
