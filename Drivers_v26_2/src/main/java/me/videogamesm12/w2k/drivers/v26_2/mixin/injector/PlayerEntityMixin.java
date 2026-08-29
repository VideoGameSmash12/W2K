package me.videogamesm12.w2k.drivers.v26_2.mixin.injector;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IEntityEntry;
import me.videogamesm12.w2k.kernel.data.IItemStackEntry;
import me.videogamesm12.w2k.toolbox.modules.BanHammer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Player.class)
public abstract class PlayerEntityMixin
{
    @Shadow
    public abstract boolean isCreative();

    @Shadow
    public abstract GameProfile getGameProfile();

    @Inject(method = "attack", at = @At(value = "RETURN"))
    public void onLeftClick(Entity entity, CallbackInfo ci)
    {
        final BanHammer banHammer = W2K.getInstance().getModuleManager().getModule(BanHammer.class);
        final ItemStack stack = LivingEntity.class.cast(this).getItemInHand(InteractionHand.MAIN_HAND);
        if (banHammer.isEnabled() && isCreative() && getGameProfile().id() == Objects.requireNonNull(Minecraft.getInstance().player).getUUID())
        {
            banHammer.handleClick((IEntityEntry) entity, IItemStackEntry.class.cast(stack), true);
        }
    }

    @Inject(method = "interactOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    public void onRightClick(Entity entity, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir, @Local ItemStack stack, @Local ItemStack copy)
    {
        final BanHammer banHammer = W2K.getInstance().getModuleManager().getModule(BanHammer.class);
        if ((banHammer.isEnabled() && isCreative() && copy != null && getGameProfile().id() == Objects.requireNonNull(Minecraft.getInstance().player).getUUID())
                && banHammer.handleClick((IEntityEntry) entity, IItemStackEntry.class.cast(copy), false))
        {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
