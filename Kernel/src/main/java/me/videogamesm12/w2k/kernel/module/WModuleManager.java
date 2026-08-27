package me.videogamesm12.w2k.kernel.module;

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
import java.util.Optional;

public class WModuleManager
{
    private static final File modulesFile = new File(W2K.getModFolder(), "modules.nbt");
    @Getter
    private final Map<ModContainer, Map<String, WModule>> registry = new HashMap<>();

    public void registerModules()
    {
        FabricLoader.getInstance().getEntrypointContainers("w2k-modules", WModule.class).forEach(container ->
        {
            // Get the mod providing this and the module itself
            final ModContainer mod = container.getProvider();
            final WModule module = container.getEntrypoint();

            // If this is the first module in this mod, we create a registry for the mod
            if (!registry.containsKey(mod))
                registry.put(mod, new HashMap<>());

            // Get the relevant registry for this mod
            final Map<String, WModule> modModuleRegistry = registry.get(mod);
            modModuleRegistry.put(mod.getMetadata().getId() + ":" + module.getId().replace(" ", "_"), module);
        });
    }

    public <T extends WModule> T getModule(final Class<T> id)
    {
        return registry.values().stream()
                .flatMap(map -> map.values().stream())
                .filter(module -> module.getClass().equals(id))
                .findAny()
                .map(module -> (T) module)
                .orElseThrow(() -> new IllegalArgumentException("Module " + id.getName() + " has not been registered"));
    }

    public void loadModules()
    {
        if (modulesFile.exists())
        {
            try
            {
                final CompoundBinaryTag root = BinaryTagIO.reader().read(modulesFile.toPath());
                registry.forEach((mod, modRegistry) ->
                        modRegistry.forEach((key, module) -> module.deserialize(root.getCompound(key))));
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
        registry.forEach((mod, modRegistry) ->
                modRegistry.forEach((key, module) -> builder.put(key, module.serialize())));

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
