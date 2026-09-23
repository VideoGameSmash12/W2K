package me.videogamesm12.w2k.kernel.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.function.Predicate;

@Getter
public class BoxOverlay extends Overlay
{
    private final int width;
    private final int height;
    private final Color color;

    public BoxOverlay(int x, int y, int width, int height, Color color, Alignment horizontalAlignment, Alignment verticalAlignment, Predicate<Overlay> shouldDisplay)
    {
        super("w2k:box", x, y, horizontalAlignment, verticalAlignment, shouldDisplay);
        this.width = width;
        this.height = height;
        this.color = color;
    }
}
