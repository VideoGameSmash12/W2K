package me.videogamesm12.w2k.blackbox.theming.flatlaf;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeProvider;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;
import me.videogamesm12.w2k.kernel.W2K;

import java.io.File;
import java.util.*;

public class FlatLAFCustomThemesProvider implements IThemeProvider
{
    private static final Map<String, ITheme> themes = new HashMap<>();
    private static final IThemeType type = new IThemeType()
    {
        @Override
        public String getLabel()
        {
            return "FlatLAF | Custom Themes";
        }

        @Override
        public int getId()
        {
            return 1338;
        }

        @Override
        public void update()
        {
            FlatLaf.updateUI();
        }
    };

    static
    {
        Arrays.stream(Objects.requireNonNull(getThemesFolder().listFiles()))
                .filter(file -> file.getName().endsWith(".properties"))
                .map(FlatLAFCustomTheme::new)
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

    public static File getThemesFolder()
    {
        File folder = new File(Blackbox.getFolder(), "themes/flatlaf");

        if (!folder.exists())
        {
            folder.mkdirs();
        }

        return folder;
    }

    @RequiredArgsConstructor
    public static class FlatLAFCustomTheme implements ITheme
    {
        private final File file;
        private FlatPropertiesLaf laf;

        @Override
        public String getInternalName()
        {
            return "flatlaf-custom-theme:" + file.getName();
        }

        @Override
        public String getThemeName()
        {
            return file.getName().replace(".properties", "");
        }

        @Override
        public String getThemeDescription()
        {
            return file.getAbsolutePath();
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
            if (laf == null)
            {
                try
                {
                    laf = new FlatPropertiesLaf(file.getName(), file);
                }
                catch (Exception ex)
                {
                    W2K.getLogger().error("Failed to load custom FlatLAF theme", ex);
                }
            }

            FlatPropertiesLaf.setup(laf);
        }

        @Override
        public void showOptionalMessage()
        {
        }
    }
}
