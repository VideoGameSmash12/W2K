package me.videogamesm12.w2k.blackbox.theming.custom.synth;

import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;
import me.videogamesm12.w2k.kernel.W2K;

import javax.swing.*;
import javax.swing.plaf.synth.SynthLookAndFeel;
import java.io.File;

public class CSTheme implements ITheme
{
    private final File file;

    public CSTheme(File file)
    {
        this.file = file;
    }

    @Override
    public String getInternalName()
    {
        return "bbCusSynth:" + file.getName();
    }

    @Override
    public String getThemeName()
    {
        return file.getName();
    }

    @Override
    public String getThemeDescription()
    {
        return null;
    }

    @Override
    public IThemeType getType()
    {
        return CSThemeProvider.TYPE;
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
        catch (Exception ex)
        {
            W2K.getLogger().error("WTF?", ex);
        }
    }

    @Override
    public void showOptionalMessage()
    {
    }
}
