package me.videogamesm12.w2k.kernel.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * <h1>MapEntry</h1>
 * <p>Version-agnostic wrapper for MapState.</p>
 */
public interface IMapEntry
{
    String w2k$id();

    String w2k$scale();

    String w2k$dimension();

    int w2k$centerX();

    int w2k$centerZ();

    boolean w2k$locked();

    byte[] w2k$colors();

    String w2k$nbt();

    List<Object> w2k$toTableRow();
    /*public List<Object> toTableRow()
    {
        return Arrays.asList(
                id,         // Map ID
                scale,      // Scale
                dimension,  // Dimension ID
                centerX,    // Center X
                centerZ,    // Center Z
                locked      // Locked
        );
    }*/
}
