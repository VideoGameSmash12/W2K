package me.videogamesm12.w2k.blackbox.theming.custom.flatlaf;

import com.formdev.flatlaf.FlatPropertiesLaf;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.blackbox.theming.IThemeType;
import me.videogamesm12.w2k.kernel.W2K;

import java.io.File;

public class CFTheme implements ITheme
{
    private final File file;
    private FlatPropertiesLaf laf;

    public CFTheme(File file)
    {
        this.file = file;
    }

    @Override
    public String getInternalName()
    {
        return "bbCusFlatLAF:" + file.getName();
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
        return CFThemeProvider.TYPE;
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
