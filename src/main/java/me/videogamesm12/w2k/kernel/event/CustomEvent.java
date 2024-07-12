package me.videogamesm12.w2k.kernel.event;

import lombok.Getter;
import lombok.Setter;

/**
 * <h1>CustomEvent</h1>
 * <p>The foundation for all custom events in W2K. It cannot be initialized on its own, but it can be extended.</p>
 */
@Setter
@Getter
public abstract class CustomEvent
{
    /**
     * Returns whether the event was cancelled. Events don't necessarily need to support being cancelled, but you're
     * strongly advised to do support it anyway.
     */
    private boolean cancelled;
}