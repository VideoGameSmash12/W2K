package me.videogamesm12.w2k.kernel.module.setting;

import lombok.Getter;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.IntBinaryTag;

@Getter
public class IntegerSetting extends WModuleSetting<IntBinaryTag, Integer>
{
    private final int minimum;
    private final int maximum;
    private final boolean spinner;

    public IntegerSetting(final String id, final String name, final int defaultValue, final int minimum, final int maximum, final boolean spinner)
    {
        super(id, name, defaultValue, BinaryTagTypes.INT.id());
        this.minimum = minimum;
        this.maximum = maximum;
        this.spinner = spinner;
    }

    public IntegerSetting(final String id, final String name, final int defaultValue, final boolean spinner)
    {
        this(id, name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, spinner);
    }

    @Override
    public void set(Integer value)
    {
        super.set(Math.max(minimum, Math.min(maximum, value)));
    }

    @Override
    public void read(IntBinaryTag wrapper)
    {
        set(wrapper.value());
    }

    @Override
    public IntBinaryTag write()
    {
        return IntBinaryTag.intBinaryTag(get());
    }
}
