package me.videogamesm12.w2k.blackbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.videogamesm12.w2k.kernel.W2K;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

@Getter
@Setter
public class Configuration
{
    private boolean enhancedListingEnabled;
    private boolean showOnStartupEnabled;
    private boolean ignoringFreezesDuringStartup = true;
    private boolean autoRefreshEnabled = true;
    private String theme = "DARK";
    private int width = 420;
    private int height = 560;

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