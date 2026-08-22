package me.videogamesm12.w2k.blackbox.theming.flatlaf;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeProvider;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;
import me.videogamesm12.w2k.kernel.W2K;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.*;

public class FlatLAFCustomIntelliJThemesProvider implements IThemeProvider
{
    private static final Map<String, ITheme> themes = new HashMap<>();
    private static final IThemeType type = new IThemeType()
    {
        @Override
        public String getLabel()
        {
            return "FlatLAF | Custom IntelliJ Themes";
        }

        @Override
        public int getId()
        {
            return 1339;
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
                .filter(file -> file.getName().endsWith(".json"))
                .map(FlatLAFCustomIntelliJTheme::new)
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
        File folder = new File(Blackbox.getFolder(), "themes/intellij");

        if (!folder.exists())
        {
            folder.mkdirs();
        }

        return folder;
    }

    @RequiredArgsConstructor
    public static class FlatLAFCustomIntelliJTheme implements ITheme
    {
        private final File file;

        @Override
        public String getInternalName()
        {
            return "flatlaf-custom-intellij-theme:" + file.getName();
        }

        @Override
        public String getThemeName()
        {
            return file.getName().replace(".json", "");
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
            try
            {
                IntelliJTheme.setup(Files.newInputStream(file.toPath()));
            }
            catch (Exception ex)
            {
                W2K.getLogger().error("Failed to load custom FlatLAF theme", ex);
                FlatDarkLaf.setup();
            }
        }

        @Override
        public void showOptionalMessage()
        {
        }
    }
}
