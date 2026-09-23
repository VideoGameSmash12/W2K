package me.videogamesm12.w2k.val.v1_21_11.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.render.EntityRenderEvent;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderManager.class)
public class EntityRenderManagerInjector
{
    @Unique
    private final EntityRenderEvent event = new EntityRenderEvent();

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void callEntityRenderEvent(E entity, Frustum frustum, double d, double e, double f, CallbackInfoReturnable<Boolean> cir)
    {
        W2K.getEventBus().post(event.update((EntityInterface) entity));

        if (event.isCancelled())
        {
            cir.setReturnValue(false);
        }
    }
}
