package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * <h1>LivingEntityInterface</h1>
 * <p>A wrapper interface for {@code LivingEntity} instances implemented using Mixins.
 * @implNote    This interface inherits all the methods from {@link EntityInterface}. However, those methods don't need
 *              to be implemented in an implementation of this class since they would already be implemented by your
 *              implementations of said interface.
 */
public interface LivingEntityInterface extends EntityInterface
{
    /**
     * <p>Gets the {@link ItemStackInterface ItemStack} currently in the entity's main hand.</p>
     * <p>Some versions of Minecraft may specify a null object if there is no such item in their hand. As such, this is
     *  an optional to allow you to perform actions only if an item is present using methods like
     *  {@link Optional#isPresent()}.</p>
     * @return  {@link Optional<ItemStackInterface>}
     */
    Optional<ItemStackInterface> w2k$getStackInMainHand();

    /**
     * <p>Gets the {@link ItemStackInterface ItemStack} currently in the entity's main hand.</p>
     * <p>Some versions of Minecraft may specify a null object if there is no such item in their hand. As such, there's
     *  a chance this will return null.</p>
     * @return  {@link ItemStackInterface}
     */
    @Nullable
    ItemStackInterface w2k$getStackInMainHandUnsafe();

    /**
     * <p>Gets the {@link ItemStackInterface ItemStack} currently in the entity's offhand.</p>
     * <p>Some versions of Minecraft may specify a null object if there is no such item in their offhand. As such, this
     *  is an optional to allow you to perform actions only if an item is present using methods like
     *  {@link Optional#isPresent()}.</p>
     * @return  {@link Optional<ItemStackInterface>}
     */
    Optional<ItemStackInterface> w2k$getStackInOffHand();

    /**
     * <p>Gets the {@link ItemStackInterface ItemStack} currently in the entity's offhand.</p>
     * <p>Some versions of Minecraft may specify a null object if there is no such item in their hand. As such, there's
     *  a chance this will return null.</p>
     * @return  {@link ItemStackInterface}
     */
    @Nullable
    ItemStackInterface w2k$getStackInOffHandUnsafe();
}
