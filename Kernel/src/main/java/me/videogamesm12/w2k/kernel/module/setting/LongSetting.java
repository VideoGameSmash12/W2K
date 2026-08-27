package me.videogamesm12.w2k.kernel.module.setting;

import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.LongBinaryTag;

public class LongSetting extends WModuleSetting<LongBinaryTag, Long>
{
    private final long minimum;
    private final long maximum;

    public LongSetting(final String id, final String name, final long defaultValue, final long minimum, final long maximum)
    {
        super(id, name, defaultValue, BinaryTagTypes.LONG.id());
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public void set(Long value)
    {
        super.set(Math.max(minimum, Long.min(maximum, value)));
    }

    @Override
    public void read(LongBinaryTag wrapper)
    {
        set(wrapper.value());
    }

    @Override
    public LongBinaryTag write()
    {
        return LongBinaryTag.longBinaryTag(get());
    }
}
