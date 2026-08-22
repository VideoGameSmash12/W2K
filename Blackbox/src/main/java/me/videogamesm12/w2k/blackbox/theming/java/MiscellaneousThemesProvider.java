package me.videogamesm12.w2k.blackbox.theming.java;

import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeProvider;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.util.*;

public class MiscellaneousThemesProvider implements IThemeProvider
{
    private static final Map<String, ITheme> themes = new HashMap<>();
    private static final IThemeType type = new IThemeType()
    {
        @Override
        public String getLabel()
        {
            return "Java | Miscellaneous Themes";
        }

        @Override
        public int getId()
        {
            return 3;
        }
    };
    static
    {
        Arrays.stream(MiscellaneousTheme.values())
                .forEach(theme -> themes.put(theme.getInternalName(), theme));
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
    public enum MiscellaneousTheme implements ITheme
    {
        NIMBUS("nimbus", new NimbusLookAndFeel());

        private final String id;
        private final LookAndFeel laf;

        @Override
        public String getInternalName()
        {
            return "java:" + id;
        }

        @Override
        public String getThemeName()
        {
            return laf.getName();
        }

        @Override
        public String getThemeDescription()
        {
            return laf.getDescription();
        }

        @Override
        public IThemeType getType()
        {
            return type;
        }

        @Override
        public String getThemeClass()
        {
            return laf.getClass().getName();
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
                UIManager.setLookAndFeel(laf);
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
