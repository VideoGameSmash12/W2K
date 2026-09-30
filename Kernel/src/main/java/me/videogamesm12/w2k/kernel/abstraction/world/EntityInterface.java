package me.videogamesm12.w2k.kernel.abstraction.world;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * <h1>EntityInterface</h1>
 * <p>A wrapper interface for {@code Entity} instances implemented using Mixins.
 */
public interface EntityInterface extends ObjectInterface
{
    /**
     * <p>Gets the entity's internal name.</p>
     * <p>This is different from both their display name and UUID as it is used by the game for handling scoreboards. As
     *  such, the output of this method depends on the type of the entity. If the entity is a player entity, this will
     *  return their username. Otherwise, it will return the entity's UUID.</p>
     * @return  {@link String}
     */
    String w2k$internalName();

    /**
     * <p>Gets the entity's display name.</p>
     * @return  {@link JsonElement}
     */
    JsonElement w2k$name();

    /**
     * <p>Gets the entity's type in the form of a stringified identifier.</p>
     * @return  {@link String}
     */
    String w2k$type();

    /**
     * Gets the entity's current X position.
     * @return  {@code double}
     */
    double w2k$x();

    /**
     * Gets the entity's current Y position.
     * @return  {@code double}
     */
    double w2k$y();

    /**
     * Gets the entity's current Z position.
     * @return  {@code double}
     */
    double w2k$z();

    /**
     * Gets the entity's current position in the form of a {@link BlockPosInterface BlockPos}.
     * @return  {@link BlockPosInterface}
     */
    BlockPosInterface w2k$blockPos();

    /**
     * <p>Gets the entity's current numerical ID.</p>
     * <p>Not to be mistaken for their {@link UUID}, which can be obtained using {@link EntityInterface#w2k$uuid()}.</p>
     * @return  {@code int}
     */
    int w2k$id();

    /**
     * Gets the entity's current UUID.
     * <p>Not to be mistaken for their numerical ID, which can be obtained using {@link EntityInterface#w2k$id()}.</p>
     * @return  {@link UUID}
     */
    UUID w2k$uuid();

    /**
     * Gets the entity's NBT data.
     * @return  {@link String}
     */
    String w2k$data();

    /**
     * Locally removes the entity from the world.
     */
    void w2k$kill();

    /**
     * <p>Creates a list of data to be displayed in components as a table row.</p>
     * <p>The data listed is as follows:</p>
     * <ul>
     *     <li>Display Name (fetched using {@link w2k$name()})</li>
     *     <li>Entity Type (fetched using {@link w2k$type()})</li>
     *     <li>Entity Position (fetched using {@link w2k$x()}, {@link w2k$y()}, and {@link w2k$z()}, then stringified
     *      with {@link String#format(String, Object...)})</li>
     *     <li>Numerical ID (fetched using {@link w2k$id()})</li>
     *     <li>UUID (fetched using {@link w2k$uuid()})</li>
     * </ul>
     * @return  {@link java.util.List}
     */
    default List<Object> w2k$toTableRow()
    {
        return Arrays.asList(
                w2k$name() != null ? w2k$val().text().jsonToString(w2k$name()) : null,  // Display Name
                w2k$type(),                                                             // Type
                String.format("%s, %s, %s", w2k$x(), w2k$y(), w2k$z()),                 // Location
                w2k$id(),                                                               // ID
                w2k$uuid().toString()                                                   // UUID
        );
    }
}
