package me.videogamesm12.w2k.kernel.graphics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Alignment
{
    LEAST(0.0F, 1.0F),
    CENTER(0.5F, 0.5F),
    MOST(1.0F, -1.0F);

    private final float offset;
    private final float screenOffset;
}
