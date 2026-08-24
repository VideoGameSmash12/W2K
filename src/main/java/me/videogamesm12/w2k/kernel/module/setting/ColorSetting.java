package me.videogamesm12.w2k.kernel.module.setting;

import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.IntArrayBinaryTag;

import java.awt.*;

public class ColorSetting extends WModuleSetting<IntArrayBinaryTag, Color>
{
    public ColorSetting(final String id, final String name, final Color defaultValue)
    {
        super(id, name, defaultValue, BinaryTagTypes.INT_ARRAY.id());
    }

    @Override
    public void read(IntArrayBinaryTag wrapper)
    {
        int red;
        int green;
        int blue;
        int alpha = 255;

        switch (wrapper.size())
        {
            case 4:
            {
                alpha = Math.max(0, Math.min(255, wrapper.get(3)));
            }
            case 3:
            {
                red = Math.max(0, Math.min(255, wrapper.get(0)));
                green = Math.max(0, Math.min(255, wrapper.get(1)));
                blue = Math.max(0, Math.min(255, wrapper.get(2)));
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Unrecognized color format: " + wrapper);
            }
        }

        set(new Color(red, green, blue, alpha));
    }

    @Override
    public IntArrayBinaryTag write()
    {
        final Color color = get();
        return IntArrayBinaryTag.intArrayBinaryTag(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
}
