package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface EntityInterface extends ObjectInterface
{
    String w2k$internalName();

    Component w2k$name();

    String w2k$type();

    double w2k$x();

    double w2k$y();

    double w2k$z();

    BlockPosInterface w2k$blockPos();

    int w2k$id();

    UUID w2k$uuid();

    String w2k$data();

    void w2k$kill();

    default List<Object> w2k$toTableRow()
    {
        return Arrays.asList(
                w2k$name() != null ? w2k$val().text().adventureToString(w2k$name()) : null,  // Display Name
                w2k$type(),                                                                                       // Type
                String.format("%s, %s, %s", w2k$x(), w2k$y(), w2k$z()),                                           // Location
                w2k$id(),                                                                                         // ID
                w2k$uuid().toString()                                                                             // UUID
        );
    }
}
