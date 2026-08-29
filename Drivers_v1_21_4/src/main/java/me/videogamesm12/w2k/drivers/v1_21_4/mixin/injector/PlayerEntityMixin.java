package me.videogamesm12.w2k.drivers.v1_21_4.mixin.injector;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IEntityEntry;
import me.videogamesm12.w2k.kernel.data.IItemStackEntry;
import me.videogamesm12.w2k.toolbox.modules.BanHammer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin
{
    @Shadow
    public abstract boolean isCreative();

    @Shadow
    public abstract GameProfile getGameProfile();

    @Inject(method = "attack", at = @At(value = "RETURN"))
    public void onLeftClick(Entity target, CallbackInfo ci)
    {
        final BanHammer banHammer = W2K.getInstance().getModuleManager().getModule(BanHammer.class);
        final ItemStack stack = LivingEntity.class.cast(this).getStackInHand(Hand.MAIN_HAND);
        if (banHammer.isEnabled() && isCreative() && stack != null && getGameProfile().getId() == Objects.requireNonNull(MinecraftClient.getInstance().player).getUuid())
        {
            banHammer.handleClick((IEntityEntry) target, IItemStackEntry.class.cast(stack), true);
        }
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;"), cancellable = true)
    public void onRightClick(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir, @Local ItemStack stack, @Local ItemStack copy)
    {
        final BanHammer banHammer = W2K.getInstance().getModuleManager().getModule(BanHammer.class);
        if ((banHammer.isEnabled() && isCreative() && copy != null && getGameProfile().getId() == Objects.requireNonNull(MinecraftClient.getInstance().player).getUuid())
                && banHammer.handleClick((IEntityEntry) entity, IItemStackEntry.class.cast(copy), false))
        {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
