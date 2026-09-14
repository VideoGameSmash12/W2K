package me.videogamesm12.w2k.val.v1_20_1.wrappers.world;

import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.LivingEntityInterface;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityWrapper implements LivingEntityInterface
{
    @Shadow
    public abstract ItemStack getStackInHand(Hand hand);

    @Override
    public Optional<ItemStackInterface> w2k$getStackInMainHand()
    {
        final ItemStack stack = getStackInHand(Hand.MAIN_HAND);
        if (stack == null)
        {
            return Optional.empty();
        }
        return Optional.of(ItemStackInterface.class.cast(stack));
    }

    @Override
    public Optional<ItemStackInterface> w2k$getStackInOffHand()
    {
        final ItemStack stack = getStackInHand(Hand.OFF_HAND);
        if (stack == null)
        {
            return Optional.empty();
        }
        return Optional.of(ItemStackInterface.class.cast(stack));
    }
}
