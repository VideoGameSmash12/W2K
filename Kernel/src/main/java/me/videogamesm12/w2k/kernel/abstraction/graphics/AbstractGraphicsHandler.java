package me.videogamesm12.w2k.kernel.abstraction.graphics;

import me.videogamesm12.w2k.kernel.graphics.DrawableObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractGraphicsHandler
{
    private final Map<Class<? extends DrawableObject<?>>, Function<? extends DrawableObject<?>, ? extends CompiledDrawableObject<?>>> compilers = new HashMap<>();

    public AbstractGraphicsHandler()
    {
        registerCoreCompilers();
    }

    public abstract void registerCoreCompilers();

    public <T extends DrawableObject<T>, C extends CompiledDrawableObject<T>> void registerCompiler(final Class<T> clazz, final Function<T, C> compiler)
    {
        compilers.put(clazz, compiler);
    }

    public <T extends DrawableObject<T>, C extends CompiledDrawableObject<T>> Function<T, C> getCompiler(final Class<T> clazz)
    {
        return (Function<T, C>) compilers.get(clazz);
    }

    public boolean hasCompiler(final DrawableObject<?> object)
    {
        return compilers.containsKey(object.getClass());
    }
}
