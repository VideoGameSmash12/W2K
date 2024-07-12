package me.videogamesm12.w2k.blackbox.theming.flatlaf;

import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeProvider;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FThemeProvider implements IThemeProvider
{
    private static final Map<String, ITheme> themes = new HashMap<>();

    static
    {
        Arrays.stream(FlatLAFThemes.values()).forEach(theme -> themes.put(theme.getInternalName(), theme));
    }

    @Override
    public Map<String, ITheme> getThemes()
    {
        return themes;
    }

    @Override
    public List<IThemeType> getTypes()
    {
        return Arrays.stream(FLThemeType.values()).collect(Collectors.toList());
    }
}
