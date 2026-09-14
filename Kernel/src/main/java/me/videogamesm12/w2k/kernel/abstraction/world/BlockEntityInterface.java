package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;

import java.util.Arrays;
import java.util.List;

public interface BlockEntityInterface extends ObjectInterface
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
}
