package me.videogamesm12.w2k.kernel.event.miscellaneous;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
public class PanicKeyCombinationEvent extends CustomEvent
{
    private final long timestamp = System.currentTimeMillis();
}
