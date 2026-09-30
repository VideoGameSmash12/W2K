package me.videogamesm12.w2k.kernel.event.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import me.videogamesm12.w2k.kernel.graphics.DrawableObject;
import me.videogamesm12.w2k.kernel.module.WModule;

/**
 * <h1>ModuleStateUpdateEvent</h1>
 * <p>Event that is called when a {@link WModule} is about to be enabled or disabled.</p>
 */
@RequiredArgsConstructor
public class ModuleStateUpdateEvent<T extends WModule> extends CustomEvent
{
    @Getter
    private final T module;
    private final boolean originalValue;
    private final boolean newValue;

    public boolean getOriginalValue()
    {
        return originalValue;
    }

    public boolean getNewValue()
    {
        return newValue;
    }
}
