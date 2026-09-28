package me.videogamesm12.w2k.kernel.abstraction.util;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;

public interface BlockPosInterface extends ObjectInterface
{
    int w2k$x();

    int w2k$y();

    int w2k$z();

    default String w2k$toString()
    {
        return String.format("%d, %d, %d", w2k$x(), w2k$y(), w2k$z());
    }
}
