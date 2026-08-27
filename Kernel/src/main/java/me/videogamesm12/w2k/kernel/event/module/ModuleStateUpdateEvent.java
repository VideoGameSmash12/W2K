package me.videogamesm12.w2k.kernel.event.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import me.videogamesm12.w2k.kernel.module.WModule;

@Getter
@RequiredArgsConstructor
public class ModuleStateUpdateEvent<T extends WModule> extends CustomEvent
{
    private final T module;
    private final boolean originalValue;
    private final boolean newValue;
}
