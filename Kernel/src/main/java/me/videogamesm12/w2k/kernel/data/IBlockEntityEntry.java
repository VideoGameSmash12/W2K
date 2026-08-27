package me.videogamesm12.w2k.kernel.data;

import java.util.Arrays;
import java.util.List;

/**
 * <h1>TileEntry</h1>
 * <p>Version-agnostic wrapper for block entities (e.g. chests, signs).</p>
 */
public interface IBlockEntityEntry
{
    String w2k$type();

    int w2k$x();

    int w2k$y();

    int w2k$z();

    String w2k$data();

    default List<Object> w2k$toTableRow()
    {
        return Arrays.asList(w2k$type(),
                String.format("%s, %s, %s", w2k$x(), w2k$y(), w2k$z()),
                w2k$data());
    }

    default String w2k$toString()
    {
        return w2k$type() + " at " + String.format("%d, %d, %d", w2k$x(), w2k$y(), w2k$z());
    }
}
