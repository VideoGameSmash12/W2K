package me.videogamesm12.w2k.val.v26_2.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.render.EntityRenderEvent;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherInjector
{
    @Unique
    private final EntityRenderEvent event = new EntityRenderEvent();

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void callEntityRenderEvent(E entity, Frustum culler, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir)
    {
        W2K.getEventBus().post(event.update((EntityInterface) entity));

        if (event.isCancelled())
        {
            cir.setReturnValue(false);
        }
    }
}
