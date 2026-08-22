package me.videogamesm12.w2k.blackbox.theming.netbeans;

import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeProvider;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;
import org.netbeans.swing.laf.dark.DarkMetalLookAndFeel;
import org.netbeans.swing.laf.dark.DarkNimbusLookAndFeel;
import org.netbeans.swing.laf.dark.DarkNimbusTheme;

import javax.swing.*;
import java.util.*;

public class NetBeansThemesProvider implements IThemeProvider
{
    private static final Map<String, ITheme> themes = new HashMap<>();
    private static final IThemeType type = new IThemeType()
    {
        @Override
        public String getLabel()
        {
            return "NetBeans | Hello Darkness My Old Friend";
        }

        @Override
        public int getId()
        {
            return 173;
        }
    };

    static
    {
        Arrays.stream(NetBeansTheme.values()).forEach(theme -> themes.put(theme.getInternalName(), theme));
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
    public enum NetBeansTheme implements ITheme
    {
        DARK_METAL("Dark Metal", "A dark variant of the Metal theme.", DarkMetalLookAndFeel.class),
        DARK_NIMBUS("Dark Nimbus", "A dark variant of the Nimbus theme.", DarkNimbusLookAndFeel.class);

        private final String name;
        private final String description;
        private final Class<?> themeClass;

        @Override
        public String getInternalName()
        {
            return "netbeans:" + name().toLowerCase();
        }

        @Override
        public String getThemeName()
        {
            return this.name;
        }

        @Override
        public String getThemeDescription()
        {
            return description;
        }

        @Override
        public IThemeType getType()
        {
            return type;
        }

        @Override
        public String getThemeClass()
        {
            return themeClass.getName();
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
                UIManager.setLookAndFeel(getThemeClass());
            }
            catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                   IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void showOptionalMessage()
        {
        }
    }
}
