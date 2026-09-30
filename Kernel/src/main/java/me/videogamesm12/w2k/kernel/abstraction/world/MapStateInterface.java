package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;

import java.util.Arrays;
import java.util.List;

/**
 * <h1>MapStateInterface</h1>
 * <p>A wrapper interface for {@code MapState} or {@code MapItemSavedData} (depending on your mappings) instances
 *  implemented using Mixins.
 */
public interface MapStateInterface extends ObjectInterface
{
    /**
     * Sets the ID of the map. This has no effect on the actual ID of the item, it's just used to display the map's ID
     *  without workarounds.
     * @param id    {@link String}
     * @return      {@link MapStateInterface}
     */
    MapStateInterface w2k$id(String id);

    /**
     * Gets the ID of the map. This will only work if {@link w2k$id(String)} had been called before.
     * @return      {@link String}
     */
    String w2k$id();

    /**
     * Gets the scale of the map.
     * @return      {@link String}
     */
    String w2k$scale();

    /**
     * Gets the dimension ID of the world where the map comes from.
     * @return      {@link String}
     */
    String w2k$dimension();

    /**
     * Gets the center X position of the map.
     * @return      {@code int}
     */
    int w2k$centerX();

    /**
     * Gets the center Z position of the map.
     * @return      {@code int}
     */
    int w2k$centerZ();

    /**
     * Gets whether the map is locked.
     * @return      {@code boolean}
     */
    boolean w2k$locked();

    /**
     * Gets a byte array of every pixel on the map.
     * @return      {@code byte[]}
     */
    byte[] w2k$colors();

    /**
     * Gets the NBT data for the map.
     * @return      {@link String}
     */
    String w2k$nbt();

    /**
     * <p>Creates a list of data to be displayed in components as a table row.</p>
     * <p>The data listed is as follows:</p>
     * <ul>
     *     <li>ID (fetched using {@link w2k$id()})</li>
     *     <li>Scale (fetched using {@link w2k$scale()})</li>
     *     <li>World Dimension ID (fetched using {@link w2k$dimension()})</li>
     *     <li>Center X (fetched using {@link w2k$centerX()})</li>
     *     <li>Center Z (fetched using {@link w2k$centerZ()})</li>
     *     <li>Locked Status (fetched using {@link w2k$locked()})</li>
     * </ul>
     * @return  {@link java.util.List}
     */
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
