package me.videogamesm12.w2k.kernel.module;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class WModuleManager
{
    private static final File modulesFile = new File(W2K.getModFolder(), "modules.nbt");
    @Getter
    private final Multimap<ModContainer, WModule> modRegistry = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(MultimapBuilder.hashKeys()).arrayListValues()).build());
    @Getter
    private final Map<Class<? extends WModule>, WModule> classRegistry = new HashMap<>();
    @Getter
    private final Map<String, WModule> idRegistry = new HashMap<>();

    public <T extends WModule> T getModule(final String id)
    {
        return (T) idRegistry.get(id);
    }

    public <T extends WModule> T getModule(final Class<T> id)
    {
        return (T) classRegistry.get(id);
    }

    public void registerModule(final ModContainer mod, final WModule module)
    {
        modRegistry.put(mod, module);
        idRegistry.put(mod.getMetadata().getId() + ":" + module.getId().replace(" ", "_"), module);
        classRegistry.put(module.getClass(), module);
    }

    public void loadModules()
    {
        if (modulesFile.exists())
        {
            try
            {
                final CompoundBinaryTag root = BinaryTagIO.reader().read(modulesFile.toPath());
                modRegistry.forEach((mod, module) ->
                        module.deserialize(root.getCompound(mod.getMetadata().getId() + ":" + module.getId().replace(" ", "_"))));
            }
            catch (IOException ex)
            {
                W2K.getLogger().error("Failed to read modules configuration", ex);
            }
        }
    }

    public void saveModules()
    {
        final CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();

        modRegistry.forEach((mod, module) ->
                builder.put(mod.getMetadata().getId() + ":" + module.getId().replace(" ", "_"), module.serialize()));

        try
        {
            BinaryTagIO.writer().write(builder.build(), modulesFile.toPath());
        }
        catch (IOException ex)
        {
            W2K.getLogger().error("Failed to write modules configuration", ex);
        }
    }
}
