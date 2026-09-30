package me.videogamesm12.w2k.kernel.abstraction.inventory;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.MapStateInterface;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

/**
 * <h1>ItemStackInterface</h1>
 * <p>A wrapper interface for {@code ItemStack} instances implemented using Mixins.</p>
 */
public interface ItemStackInterface extends ObjectInterface
{
    /**
     * Gets the stack's display name.
     * @return              {@link JsonElement}
     */
    JsonElement w2k$name();

    /**
     * Gets the stack's type in the form of a stringified identifier.
     * @return              {@link String}
     */
    String w2k$type();

    /**
     * Gets the stack's count.
     * @return              {@code int}
     */
    int w2k$count();

    /**
     * Gets the stack's damage value.
     * @return              {@code int}
     */
    int w2k$damage();

    /**
     * Gets the location of the stack. This will only work if {@link w2k$location(String)} had been called before.
     * @return              {@link String}
     */
    String w2k$location();

    /**
     * Sets the location of the stack. This has no effect on the actual placement of the item, it's just used to display
     *  the item's location without too much of a workaround.
     * @param location      {@link String}
     * @return              {@link ItemStackInterface}
     */
    ItemStackInterface w2k$location(String location);

    /**
     * Gets the NBT data of the stack.
     * @return              {@link String}
     */
    String w2k$data();

    /**
     * <p>Creates a list of data to be displayed in components as a table row.</p>
     * <p>The data listed is as follows:</p>
     * <ul>
     *     <li>Display Name (fetched using {@link w2k$name()})</li>
     *     <li>Type (fetched using {@link w2k$type()})</li>
     *     <li>Count (fetched using {@link w2k$count()})</li>
     *     <li>Damage (fetched using {@link w2k$damage()})</li>
     *     <li>Location (fetched using {@link w2k$location()})</li>
     *     <li>NBT (fetched using {@link w2k$data()})</li>
     * </ul>
     * @return  {@link java.util.List}
     */
    default List<Object> w2k$toTableRow()
    {
        return Arrays.asList(
                w2k$name() != null ?
                        w2k$val().text().jsonToString(w2k$name()) : null,       // Display Name
                w2k$type(),                                                     // ID
                w2k$count(),                                                    // Count
                w2k$damage(),                                                   // Damage
                w2k$location(),                                                 // Location
                w2k$data()                                                      // Data
        );
    }

    /**
     * Gets whether the stack actually has anything.
     * @return          {@code boolean}
     */
    default boolean w2k$isNotEmpty()
    {
        return !w2k$type().equalsIgnoreCase("minecraft:air");
    }
}
