package me.videogamesm12.w2k.kernel.module.setting;

import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.ByteBinaryTag;

public class BooleanSetting extends WModuleSetting<ByteBinaryTag, Boolean>
{
    public BooleanSetting(final String id, final String name, final Boolean defaultValue)
    {
        super(id, name, defaultValue, BinaryTagTypes.BYTE.id());
    }

    @Override
    public void read(ByteBinaryTag wrapper)
    {
        set(wrapper.value() == 1);
    }

    @Override
    public ByteBinaryTag write()
    {
        return get() ? ByteBinaryTag.ONE : ByteBinaryTag.ZERO;
    }
}
