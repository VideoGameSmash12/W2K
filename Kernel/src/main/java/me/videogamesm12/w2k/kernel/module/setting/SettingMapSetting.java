package me.videogamesm12.w2k.kernel.module.setting;

import me.videogamesm12.w2k.kernel.W2K;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.CompoundBinaryTag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SettingMapSetting extends WModuleSetting<CompoundBinaryTag, Map<String, WModuleSetting<?, ?>>>
{
    public SettingMapSetting(final String id, final String name, final WModuleSetting<?, ?>... entries)
    {
        super(id, name, new HashMap<>(), BinaryTagTypes.COMPOUND.id());
        Arrays.stream(entries).forEach(entry -> get().put(entry.getId(), entry));
    }

    @Override
    public void read(CompoundBinaryTag wrapper)
    {
        if (wrapper == null || wrapper.size() == 0)
        {
            return;
        }

        wrapper.forEach(entry ->
        {
            final String key = entry.getKey();

            if (!get().containsKey(key))
            {
                W2K.getLogger().warn("Ignoring unknown setting {} in setting map {}", key, getId());
                return;
            }

            final WModuleSetting<BinaryTag, Object> setting = (WModuleSetting<BinaryTag, Object>) get().get(key);
            if (setting.getType() != entry.getValue().type().id())
            {
                W2K.getLogger().warn("Ignoring invalid value for setting {} in setting map {}", key, getId());
                return;
            }

            setting.read(entry.getValue());
        });
    }

    @Override
    public CompoundBinaryTag write()
    {
        final CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();
        get().forEach((id, setting) -> builder.put(id, setting.write()));
        return builder.build();
    }

    public <Wrapper extends BinaryTag, Raw, Setting extends WModuleSetting<Wrapper, Raw>> Setting get(String id)
    {
        if (!get().containsKey(id))
        {
            throw new IllegalArgumentException("Unknown setting: " + id);
        }

        return (Setting) get().get(id);
    }
}
