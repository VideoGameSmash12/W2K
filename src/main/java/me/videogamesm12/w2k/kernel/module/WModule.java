package me.videogamesm12.w2k.kernel.module;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver;
import me.videogamesm12.w2k.kernel.module.setting.WModuleSetting;
import net.kyori.adventure.nbt.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public abstract class WModule
{
    private final String id;
    private final String name;
    private final String description;
    private final Consumer<Boolean> onToggle;
    private final Map<String, WModuleSetting<? extends BinaryTag, ?>> settings = new HashMap<>();
    private boolean enabled;

    public WModule(final String name, final String description)
    {
        this.id = name.toLowerCase();
        this.name = name;
        this.description = description;
        this.onToggle = null;
    }

    public WModule(final String id, final String name, final String description, final Consumer<Boolean> onToggle)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.onToggle = onToggle;
    }

    public <T extends BinaryTag, R, W extends WModuleSetting<T, R>> W register(W setting)
    {
        settings.put(setting.getId(), setting);
        return setting;
    }

    public void setEnabled(boolean value)
    {
        this.enabled = value;
        // TODO: Setup event for this lol
    }

    public CompoundBinaryTag serialize()
    {
        // Create root compound
        final CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();
        builder.putBoolean("enabled", enabled);

        // Create settings compound
        final CompoundBinaryTag.Builder settingsBuilder = CompoundBinaryTag.builder();
        settings.forEach((key, value) -> settingsBuilder.put(key, value.write()));
        builder.put("settings", settingsBuilder.build());

        // Return root compound
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    public void deserialize(final CompoundBinaryTag tag)
    {
        // If invalid or nonsensical, do nothing
        if (tag == null || tag.size() == 0)
        {
            return;
        }

        // Read enabled state
        this.enabled = tag.getBoolean("enabled", false);

        // Read settings
        final CompoundBinaryTag settingsTag = tag.getCompound("settings");
        if (settingsTag.size() > 0)
        {
            settingsTag.forEach(entry ->
            {
                final String key = entry.getKey();

                if (!settings.containsKey(key))
                {
                    W2K.getLogger().warn("Ignoring unknown setting {} in module {}", key, id);
                    return;
                }

                // iT's RaW
                final WModuleSetting setting = settings.get(key);

                if (setting.getType() != entry.getValue().type().id())
                {
                    W2K.getLogger().warn("Ignoring invalid value for setting {} in module {}", key, id);
                    return;
                }

                // UnCheCKeD CaLl
                try
                {
                    setting.read(entry.getValue());
                }
                catch (Throwable ex)
                {
                    W2K.getLogger().error("Unable to read value for setting {} in module {}", key, id, ex);
                }
            });
        }
    }

    protected W2K w2k()
    {
        return W2K.getInstance();
    }

    protected WVersionBridgeDriver versionBridge()
    {
        return w2k().getDriverManager().getVersionBridge();
    }
}
