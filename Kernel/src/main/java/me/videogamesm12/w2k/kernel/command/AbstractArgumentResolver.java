package me.videogamesm12.w2k.kernel.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class AbstractArgumentResolver<T> // Type
{
    private final String identifier;
    private final Class<T> rawClass;

    public abstract T resolveArgument(final String string);
}
