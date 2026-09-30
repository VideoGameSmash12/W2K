package me.videogamesm12.w2k.kernel.abstraction.graphics;

import me.videogamesm12.w2k.kernel.graphics.DrawableObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * <h1>AbstractGraphicsHandler</h1>
 * <p>A class responsible for registering and managing the compilers for {@link DrawableObject} instances.</p>
 */
public abstract class AbstractGraphicsHandler
{
    private final Map<Class<? extends DrawableObject<?>>, Function<? extends DrawableObject<?>, ? extends CompiledDrawableObject<?>>> compilers = new HashMap<>();

    public AbstractGraphicsHandler()
    {
        registerCoreCompilers();
    }

    /**
     * <p>Register the compilers for {@link DrawableObject} instances implemented within the Kernel itself.</p>
     * <p>This gets called immediately after an implementation of this class gets initialized.</p>
     */
    public abstract void registerCoreCompilers();

    /**
     * Registers a compiler for a given type of {@link DrawableObject}.
     * @param clazz     {@link Class}
     * @param compiler  {@link Function}
     * @param <T>       An implementation of {@link DrawableObject}
     * @param <C>       An implementation of {@link CompiledDrawableObject}
     */
    public <T extends DrawableObject<T>, C extends CompiledDrawableObject<T>> void registerCompiler(final Class<T> clazz, final Function<T, C> compiler)
    {
        compilers.put(clazz, compiler);
    }

    /**
     * Gets a compiler for a given type of {@link DrawableObject}.
     * @param clazz     {@link Class}
     * @return          {@link Function}
     * @param <T>       An implementation of {@link DrawableObject}
     * @param <C>       An implementation of {@link CompiledDrawableObject}
     */
    public <T extends DrawableObject<T>, C extends CompiledDrawableObject<T>> Function<T, C> getCompiler(final Class<T> clazz)
    {
        return (Function<T, C>) compilers.get(clazz);
    }

    /**
     * Checks whether a given type of {@link DrawableObject} has a registered compiler.
     * @param object    {@link DrawableObject}
     * @return          {@code boolean}
     */
    public boolean hasCompiler(final DrawableObject<?> object)
    {
        return compilers.containsKey(object.getClass());
    }
}
