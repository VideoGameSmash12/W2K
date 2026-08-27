package me.videogamesm12.w2k.kernel.module.setting;

import lombok.Getter;
import net.kyori.adventure.nbt.BinaryTag;

@Getter
public abstract class WModuleSetting<W extends BinaryTag, R>
{
    private final String id;
    private final String name;
    private final R defaultValue;
    private final byte type;
    private R value;

    public WModuleSetting(final String id, final String name, final R defaultValue, final byte type)
    {
        this.id = id;
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = type;

        // We set it to the default on startup, and then we update it when we deserialize it
        this.value = defaultValue;
    }

    public R get()
    {
        return value;
    }

    public void set(R value)
    {
        this.value = value;
    }

    public abstract void read(W wrapper);

    public abstract W write();
}
