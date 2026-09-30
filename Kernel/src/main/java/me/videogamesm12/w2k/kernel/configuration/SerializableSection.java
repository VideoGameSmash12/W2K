package me.videogamesm12.w2k.kernel.configuration;

import net.kyori.adventure.nbt.CompoundBinaryTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface SerializableSection
{
    CompoundBinaryTag toNbt();

    static <T extends SerializableSection> T fromNbt(final Class<T> clazz, final CompoundBinaryTag tag)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        final Method method = clazz.getDeclaredMethod("fromNbt", CompoundBinaryTag.class);
        return (T) method.invoke(null, tag);
    }
}
