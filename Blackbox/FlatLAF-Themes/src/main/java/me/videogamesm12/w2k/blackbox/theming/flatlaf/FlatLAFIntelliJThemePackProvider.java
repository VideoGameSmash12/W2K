package me.videogamesm12.w2k.blackbox.theming.flatlaf;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeProvider;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FlatLAFIntelliJThemePackProvider implements IThemeProvider
{
    private static final Map<String, ITheme> themes = new HashMap<>();
    private static final IThemeType type = new IThemeType()
    {
        @Override
        public String getLabel()
        {
            return "FlatLAF | IntelliJ Theme Pack";
        }

        @Override
        public int getId()
        {
            return 1337;
        }

        @Override
        public void update()
        {
            FlatLaf.updateUI();
        }
    };

    static
    {
        Arrays.stream(FlatAllIJThemes.INFOS)
                .map(FlatLAFIntelliJTheme::new)
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
    public static class FlatLAFIntelliJTheme implements ITheme
    {
        private final FlatAllIJThemes.FlatIJLookAndFeelInfo info;

        @Override
        public String getInternalName()
        {
            return "flatlaf-intellij:" + info.getClassName();
        }

        @Override
        public String getThemeName()
        {
            return info.getName().replaceAll(" \\(Material\\)", "");
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
                final Class<?> themeClass = Class.forName(info.getClassName());
                themeClass.getMethod("setup").invoke(this);
            }
            catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException ignored)
            {
                com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialDarkerIJTheme.setup();
            }
        }

        @Override
        public void showOptionalMessage()
        {
        }
    }
}
