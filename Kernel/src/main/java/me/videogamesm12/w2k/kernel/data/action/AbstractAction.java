package me.videogamesm12.w2k.kernel.data.action;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractAction<T>
{
    private final String id;
    private final String label;
    private final String description;

    public abstract T performAction(final Object... arguments);
}
