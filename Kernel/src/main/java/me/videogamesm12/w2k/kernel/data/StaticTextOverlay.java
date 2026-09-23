package me.videogamesm12.w2k.kernel.data;

import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.function.Predicate;

public class StaticTextOverlay extends TextOverlay
{
    public StaticTextOverlay(final int x,
                             final int y,
                             final Alignment horizontalAlignment,
                             final Alignment verticalAlignment,
                             final Predicate<Overlay> shouldDisplay,
                             final List<Component> content,
                             final boolean shadowEnabled)
    {
        super(x, y, horizontalAlignment, verticalAlignment, shouldDisplay, () -> content, () -> 0L, shadowEnabled);
    }

    @Override
    public boolean shouldUpdate()
    {
        return false;
    }
}
