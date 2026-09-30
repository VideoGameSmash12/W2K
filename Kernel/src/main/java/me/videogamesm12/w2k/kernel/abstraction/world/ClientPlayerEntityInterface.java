package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;

/**
 * <h1>ClientPlayerEntityInterface</h1>
 * <p>A wrapper interface for {@code ClientPlayerEntity} or {@code LocalPlayer} (depending on your mappings) instance
 *  implemented using Mixins.</p>
 * @implNote    This interface inherits all the methods from {@link PlayerEntityInterface} and (by extension)
 *              {@link PlayerEntityInterface} and {@link EntityInterface}. However, those methods don't need to be
 *              implemented in an implementation of this class since they would already be implemented by your
 *              implementations of such interfaces.
 */
public interface ClientPlayerEntityInterface extends PlayerEntityInterface
{
    /**
     * Show a message to the player via the in-game chat HUD.
     * @param component {@link Component}
     */
    void w2k$displayMessage(final Component component);

    /**
     * Gets all {@link ItemStackInterface ItemStack}s currently in the player's inventory.
     * @return  {@link List}
     */
    List<ItemStackInterface> w2k$getInventory();
}