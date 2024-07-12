package me.videogamesm12.w2k.kernel.driver.base;

import me.videogamesm12.w2k.kernel.W2K;

public interface WDriver
{
    default String name()
    {
        return getClass().getName();
    }

    default WDriverMetadata getMetadata()
    {
        final Class<? extends WDriver> driverClass = getClass();
        return driverClass.isAnnotationPresent(WDriverMetadata.class) ? driverClass.getAnnotation(WDriverMetadata.class) : null;
    }

    default boolean isSupported()
    {
        final WDriverMetadata metadata = getMetadata();

        if (metadata == null)
        {
            W2K.getLogger().warn("Ignoring invalid driver {} as it is missing important data", name());
            return false;
        }


        return true;
    }

    default void onInitialize()
    {
    }
}
