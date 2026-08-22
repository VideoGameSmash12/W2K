package me.videogamesm12.w2k.blackbox.theming.flatlaf;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeProvider;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FlatLAFBuiltInThemesProvider implements IThemeProvider
{
    private static final Map<String, ITheme> themes = new HashMap<>();
    private static final IThemeType type = new IThemeType()
    {
        @Override
        public String getLabel()
        {
            return "FlatLAF | Built-in Themes";
        }

        @Override
        public int getId()
        {
            return 1336;
        }

        @Override
        public void update()
        {
            FlatLaf.updateUI();
        }
    };

    static
    {
        Arrays.stream(FlatLAFBuiltInTheme.values()).forEach(theme -> themes.put(theme.getInternalName(), theme));
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
    public enum FlatLAFBuiltInTheme implements ITheme
    {
        LIGHT("Light", "A basic light theme.", FlatLightLaf.class),
        DARK("Dark", "A basic dark theme.", FlatDarkLaf.class),
        INTELLIJ("IntelliJ", "A theme emulating the look and feel of IntelliJ IDEA versions 2019.2+ in light mode.", FlatIntelliJLaf.class),
        DARCULA("Darcula", "A theme emulating the look and feel of IntelliJ IDEA versions 2019.2+ in dark mode.", FlatDarculaLaf.class),
        MACOS_LIGHT("macOS Light", "A theme emulating the look and feel of macOS in light mode.", FlatMacLightLaf.class),
        MACOS_DARK("macOS Dark", "A theme emulating the look and feel of macOS in dark mode.", FlatMacDarkLaf.class);

        private final String name;
        private final String description;
        private final Class<?> themeClass;

        @Override
        public String getInternalName()
        {
            return "flatlaf-builtin:" + name().toLowerCase();
        }

        @Override
        public String getThemeName()
        {
            return this.name;
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
                themeClass.getMethod("setup").invoke(null);
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex)
            {
                ex.printStackTrace();
                FlatDarkLaf.setup();
            }
        }

        @Override
        public void showOptionalMessage()
        {

        }
    }
}
