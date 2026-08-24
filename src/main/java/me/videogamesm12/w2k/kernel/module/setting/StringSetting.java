package me.videogamesm12.w2k.kernel.module.setting;

import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.StringBinaryTag;

public class StringSetting extends WModuleSetting<StringBinaryTag, String>
{
    public StringSetting(final String id, final String name, final String defaultValue)
    {
        super(id, name, defaultValue, BinaryTagTypes.STRING.id());
    }

    @Override
    public void read(StringBinaryTag wrapper)
    {
        set(wrapper.value());
    }

    @Override
    public StringBinaryTag write()
    {
        return StringBinaryTag.stringBinaryTag(get());
    }
}
