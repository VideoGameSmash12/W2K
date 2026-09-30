package me.videogamesm12.w2k.kernel.abstraction.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <h1>AbstractArgumentResolver</h1>
 * <p>An object that resolves a command argument from a given String.</p>
 * @param <T>           Object
 */
@Getter
@RequiredArgsConstructor
public abstract class AbstractArgumentResolver<T> // Type
{
    /**
     * The identifier for this argument resolver.
     */
    private final String identifier;
    /**
     * The object's class.
     */
    private final Class<T> rawClass;

    /**
     * Creates or resolves an object based on the given String.
     * @param string    {@link String}
     * @return          Object
     */
    public abstract T resolveArgument(final String string);
}
