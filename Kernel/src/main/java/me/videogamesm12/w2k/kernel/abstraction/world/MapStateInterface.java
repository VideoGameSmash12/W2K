package me.videogamesm12.w2k.kernel.abstraction.world;

import java.util.Arrays;
import java.util.List;

public interface MapStateInterface
{
    MapStateInterface w2k$id(String id);

    String w2k$id();

    String w2k$scale();

    String w2k$dimension();

    int w2k$centerX();

    int w2k$centerZ();

    boolean w2k$locked();

    byte[] w2k$colors();

    String w2k$nbt();

    default List<Object> w2k$toTableRow()
    {
        return Arrays.asList(
                w2k$id(),             // Map ID
                w2k$scale(),          // Scale
                w2k$dimension(),      // Dimension ID
                w2k$centerX(),        // Center X
                w2k$centerZ(),        // Center Z
                w2k$locked());        // Locked
    }
}
