package me.videogamesm12.w2k.blackbox.theming.custom.synth;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeProvider;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;

import java.io.File;
import java.util.*;

public class CSThemeProvider implements IThemeProvider
{
    public static final IThemeType TYPE = new IThemeType()
    {
        @Override
        public String getLabel()
        {
            return "Synth Themes";
        }

        @Override
        public int getId()
        {
            return 1337420;
        }
    };

    @Override
    public Map<String, ITheme> getThemes()
    {
        Map<String, ITheme> themeMap = new HashMap<>();

        Arrays.stream(Objects.requireNonNull(getThemesFolder().listFiles())).filter(file -> file.getName().endsWith(".xml")).forEach(file ->
        {
            CSTheme theme = new CSTheme(file);
            themeMap.put(theme.getThemeName(), theme);
        });

        return themeMap;
    }

    @Override
    public List<IThemeType> getTypes()
    {
        return Collections.singletonList(TYPE);
    }

    public static File getThemesFolder()
    {
        File folder = new File(Blackbox.getFolder(), "themes/synth");

        if (!folder.exists())
        {
            folder.mkdirs();
        }

        return folder;
    }
}