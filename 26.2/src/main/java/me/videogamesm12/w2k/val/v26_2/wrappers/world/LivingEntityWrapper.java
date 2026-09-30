package me.videogamesm12.w2k.val.v26_2.wrappers.world;

import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.LivingEntityInterface;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityWrapper implements LivingEntityInterface
{
    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand hand);

    @Override
    public Optional<ItemStackInterface> w2k$getStackInMainHand()
    {
        final ItemStack stack = getItemInHand(InteractionHand.MAIN_HAND);
        if (stack == null)
        {
            return Optional.empty();
        }
        return Optional.of(ItemStackInterface.class.cast(stack));
    }

    @Override
    public ItemStackInterface w2k$getStackInMainHandUnsafe()
    {
        final ItemStack stack = getItemInHand(InteractionHand.MAIN_HAND);
        if (stack == null)
        {
            return null;
        }
        return ItemStackInterface.class.cast(stack);
    }

    @Override
    public Optional<ItemStackInterface> w2k$getStackInOffHand()
    {
        final ItemStack stack = getItemInHand(InteractionHand.OFF_HAND);
        if (stack == null)
        {
            return Optional.empty();
        }
        return Optional.of(ItemStackInterface.class.cast(stack));
    }

    @Override
    public ItemStackInterface w2k$getStackInOffHandUnsafe()
    {
        final ItemStack stack = getItemInHand(InteractionHand.OFF_HAND);
        if (stack == null)
        {
            return null;
        }
        return ItemStackInterface.class.cast(stack);
    }
}
