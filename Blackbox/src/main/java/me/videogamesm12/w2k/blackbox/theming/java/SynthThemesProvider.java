package me.videogamesm12.w2k.blackbox.theming.java;

import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeProvider;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;
import me.videogamesm12.w2k.kernel.W2K;

import javax.swing.*;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SynthThemesProvider implements IThemeProvider
{
    private static final Map<String, ITheme> themes = new HashMap<>();
    private static final IThemeType type = new IThemeType()
    {
        @Override
        public String getLabel()
        {
            return "Java | Custom Synth Themes";
        }

        @Override
        public int getId()
        {
            return 2;
        }
    };

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
    public static class SynthTheme implements ITheme
    {
        private final File file;

        @Override
        public String getInternalName()
        {
            return "java-synth:" + file.getName();
        }

        @Override
        public String getThemeName()
        {
            return file.getName();
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
            try
            {
                SynthLookAndFeel synth = new SynthLookAndFeel();
                synth.load(file.toPath().toUri().toURL());
                UIManager.setLookAndFeel(synth);
            }
            catch (ParseException | UnsupportedLookAndFeelException | IOException ex)
            {
                W2K.getLogger().error("Wtf?", ex);
            }
        }

        @Override
        public void showOptionalMessage()
        {
        }
    }
}
