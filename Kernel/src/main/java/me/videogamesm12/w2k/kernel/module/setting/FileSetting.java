package me.videogamesm12.w2k.kernel.module.setting;

import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.StringBinaryTag;

import java.io.File;

public class FileSetting extends WModuleSetting<StringBinaryTag, File>
{
    public FileSetting(final String id, final String name, final File defaultValue)
    {
        super(id, name, defaultValue, BinaryTagTypes.STRING.id());
    }

    @Override
    public void read(StringBinaryTag wrapper)
    {
        set(new File(wrapper.value()));
    }

    @Override
    public StringBinaryTag write()
    {
        return StringBinaryTag.stringBinaryTag(get().getAbsolutePath());
    }
}
