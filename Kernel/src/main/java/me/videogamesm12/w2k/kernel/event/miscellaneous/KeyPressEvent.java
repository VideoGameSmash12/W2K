package me.videogamesm12.w2k.kernel.event.miscellaneous;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
public class KeyPressEvent extends CustomEvent
{
    private final Object sync = new Object();

    private int modifiers;
    private int keyCode;

    public KeyPressEvent update(final int modifiers, final int keyCode)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.modifiers = modifiers;
            this.keyCode = keyCode;
            return this;
        }
    }
}
