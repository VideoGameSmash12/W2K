package me.videogamesm12.w2k.kernel.module.setting;

import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StringListSetting extends WModuleSetting<ListBinaryTag, List<String>>
{
    public StringListSetting(String id, String name, List<String> defaultValue)
    {
        super(id, name, new ArrayList<>(defaultValue), BinaryTagTypes.LIST.id());
    }

    @Override
    public void read(ListBinaryTag wrapper)
    {
        final List<String> strings = new ArrayList<>();
        wrapper.forEach(tag ->
        {
            if (tag instanceof StringBinaryTag)
            {
                strings.add(((StringBinaryTag) tag).value());
            }
        });
        set(strings);
    }

    @Override
    public ListBinaryTag write()
    {
        return ListBinaryTag.listBinaryTag(BinaryTagTypes.STRING, get().stream()
                .map(StringBinaryTag::stringBinaryTag)
                .collect(Collectors.toList()));
    }
}
