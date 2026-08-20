package me.videogamesm12.w2k.blackbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import lombok.Getter;
import lombok.Setter;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.util.SysUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

@Getter
@Setter
public class Configuration
{
    private boolean enhancedListingEnabled = false;
    private boolean showOnStartupEnabled = false;
    private boolean systemTrayEnabled = true;
    private boolean ignoringFreezesDuringStartup = true;
    private boolean autoRefreshEnabled = true;
    private String theme = getDefaultTheme();
    private int width = 420;
    private int height = 560;

    private String getDefaultTheme()
    {
        final SysUtils.OperatingSystemVersion version = SysUtils.getOperatingSystemVersion();

        switch (SysUtils.getOperatingSystem())
        {
            case WINDOWS:
            {
                // Windows XP to Windows 8.1
                if (version.getMajor() == 5 && version.getMinor() > 0 || version.getMajor() == 6 && version.getMinor() <= 0)
                {
                    return "WINDOWS";
                }
                // Windows 95 to Windows ME, Windows NT 4.0 to Windows 2000
                else if (version.getMajor() < 5 || version.getMajor() == 5 && version.getMinor() == 0)
                {
                    return "WINDOWS_CLASSIC";
                }
                // Windows 10+
                else if (version.getMajor() >= 10)
                {
                    // Read the registry values related to light and dark themes to figure it out
                    return Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize", "AppsUseLightTheme") == 1 ? "LIGHT" : "DARK";
                }
            }
            case MAC_OS:
            {
                // TODO: I have absolutely no way of testing whether any of this works. Contributions in this category
                //  are welcome since I sure as hell can't test it with my current setup

                // Fallback for in case the user is somehow on Mac OS 9
                if (version.getMajor() < 10)
                {
                    return "SYSTEM";
                }

                /* Mac OS X Yosemite, in addition to scrapping the skeuomorphic look of the previous iterations, also
                   added a fancy dark mode to the operating system. As there is no good way to determine through APIs if
                   someone is using the macOS dark mode, we need to do a really nasty hack.

                   The following dumpster fire code was brought to you by StackOverflow. */
                if (version.getMinor() >= 10)
                {
                    try
                    {
                        final Process proc = SysUtils.execute("defaults", "read", "-g", "AppleInterfaceStyle");
                        return proc.waitFor() == 0 ? "MAC_DARK" : "MAC_LIGHT";
                    }
                    catch (Throwable ex)
                    {
                        return "MAC_LIGHT";
                    }
                }
                else
                {
                    return "NIMBUS";
                }
            }
            case LINUX:
            case SOLARIS:
            {
                return "GTK";
            }
            default:
            {
                return "DARK";
            }
        }
    }

    public static Configuration load()
    {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "w2k-blackbox.json");

        if (file.exists())
        {
            try
            {
                return new Gson().fromJson(new FileReader(file), Configuration.class);
            }
            catch (Exception ex)
            {
                W2K.getLogger().error("Failed to load Blackbox configuration", ex);
                return new Configuration();
            }
        }
        else
        {
            return new Configuration();
        }
    }

    public static void save(Configuration config)
    {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "w2k-blackbox.json");

        try (FileWriter writer = new FileWriter(file))
        {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(config));
        }
        catch (Exception ex)
        {
            W2K.getLogger().error("Failed to write Blackbox configuration", ex);
        }
    }
}