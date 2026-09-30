package me.videogamesm12.w2k.kernel.abstraction.command;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.graphics.DrawableObject;

import java.util.List;

/**
 * <h1>EntitySelectorInterface</h1>
 * <p>A wrapper interface for {@code EntitySelector} instances implemented using Mixins.</p>
 */
public interface EntitySelectorInterface extends ObjectInterface
{
    /**
     * Get a list of entities currently in memory that match the parameters given to this selector.
     * @return  {@link List<EntityInterface>}
     */
    List<EntityInterface> w2k$getClientEntities();
}
