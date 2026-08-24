package me.videogamesm12.w2k.toolbox.modules;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.BooleanSetting;
import me.videogamesm12.w2k.kernel.module.setting.FileSetting;
import me.videogamesm12.w2k.toolbox.data.DumpResult;
import me.videogamesm12.w2k.toolbox.util.DumpUtil;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.TagStringIO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QueryLogger extends WModule
{
    public final FileSetting location = register(new FileSetting("location", "Location", DumpUtil.getDumpsFolder()));
    public final BooleanSetting alert = register(new BooleanSetting("alert", "Show Alert", true));

    public QueryLogger()
    {
        super("QueryLogger",
                "Dumps 'F3 + I' query responses received from the server to their own dedicated dump files.");
    }

    public CompletableFuture<DumpResult> logQueryResult(final String id, final String tag)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            final List<String> successful = new ArrayList<>();
            final List<String> failed = new ArrayList<>();

            final long timestamp = System.currentTimeMillis();
            final String name = id.replace(":", "-") + "-" + timestamp;

            File file = new File(location.get(), name + ".nbt");

            try (FileOutputStream stream = new FileOutputStream(file))
            {
                BinaryTagIO.writer().write(TagStringIO.get().asCompound(tag), stream, BinaryTagIO.Compression.GZIP);
                successful.add(file.getAbsolutePath());
            }
            catch (IOException ex)
            {
                // Fallback to saving files as SNBT
                if (file.exists())
                {
                    file.delete();
                }
                file = new File(location.get(), name + ".snbt");

                try (FileWriter writer = new FileWriter(file))
                {
                    writer.write(tag);
                    successful.add(file.getAbsolutePath());
                }
                catch (IOException ex2)
                {
                    // If both failed, oh well. We tried.
                    failed.add(file.getAbsolutePath());
                    W2K.getLogger().error("Failed to dump NBT from string", ex2);
                }
            }

            return DumpResult.builder()
                    .successful(successful)
                    .failed(failed)
                    .ignored(Collections.emptyList())
                    .outputDirectory(location.get()).build();
        });
    }
}
