package me.videogamesm12.w2k.kernel.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * <h1>MapEntry</h1>
 * <p>Version-agnostic wrapper for MapState.</p>
 */
@Getter
@RequiredArgsConstructor
public final class MapEntry
{
    private final String id;

    private final String scale;

    private final String dimension;

    private final int centerX;

    private final int centerZ;

    private final boolean locked;

    private final byte[] colors;

    public List<Object> toTableRow()
    {
        return Arrays.asList(
                id,         // Map ID
                scale,      // Scale
                dimension,  // Dimension ID
                centerX,    // Center X
                centerZ,    // Center Z
                locked      // Locked
        );
    }
}
