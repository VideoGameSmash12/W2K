package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;

import java.util.List;
import java.util.Map;

/**
 * <h1>ClientWorldInterface</h1>
 * <p>A wrapper interface for {@code ClientWorld} or {@code ClientLevel} (depending on your mappings) instances
 *  implemented using Mixins.
 */
public interface ClientWorldInterface extends ObjectInterface
{
    /**
     * Gets a List of all entities currently in memory.
     * @return  {@link List}
     */
    List<EntityInterface> w2k$getEntities();

    /**
     * Gets a List of all valid block entities currently in memory.
     * @return  {@link List}
     */
    List<BlockEntityInterface> w2k$getBlockEntities();

    /**
     * Gets a Map of all map data currently in memory.
     * @return  {@link Map}
     */
    Map<String, MapStateInterface> w2k$getMapStates();
}
