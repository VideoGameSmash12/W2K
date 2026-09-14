package me.videogamesm12.w2k.kernel.abstraction.inventory;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

public interface ItemStackInterface extends ObjectInterface
{
    Component w2k$name();

    String w2k$type();

    int w2k$count();

    int w2k$damage();

    String w2k$location();

    ItemStackInterface w2k$location(String location);

    String w2k$data();

    default List<Object> w2k$toTableRow()
    {
        return Arrays.asList(
                w2k$name() != null ?
                        w2k$val().text().adventureToString(w2k$name()) : null,  // Display Name
                w2k$type(),                                                     // ID
                w2k$count(),                                                    // Count
                w2k$damage(),                                                   // Damage
                w2k$location(),                                                 // Location
                w2k$data()                                                      // Data
        );
    }

    default boolean w2k$isNotEmpty()
    {
        return !w2k$type().equalsIgnoreCase("minecraft:air");
    }
}
