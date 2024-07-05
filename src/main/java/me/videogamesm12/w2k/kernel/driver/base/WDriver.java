package me.videogamesm12.w2k.kernel.driver.base;

public interface WDriver
{
    default String name()
    {
        return getClass().getName();
    }

    default boolean isSupported()
    {
        final Class<? extends WDriver> driverClass = getClass();

        if (driverClass.isAnnotationPresent(WDriverMetadata.class))
        {
            driverClass.getAnnotation(WDriverMetadata.class);
        }

        return true;
    }
}
