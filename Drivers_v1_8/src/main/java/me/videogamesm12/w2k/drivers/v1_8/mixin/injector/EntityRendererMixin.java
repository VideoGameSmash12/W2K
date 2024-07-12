package me.videogamesm12.w2k.drivers.v1_8.mixin.injector;

import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererMixin
{
    @Inject(method = "method_6913", at = @At("HEAD"), cancellable = true)
    public void injectRenderEntity(Entity entity, double d, double e, double f, float g, float h, boolean bl, CallbackInfoReturnable<Boolean> cir)
    {
        if (Supervisor.getConfig().getRenderingSettings().isEntityRenderingDisabled()
                || Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled())
        {
            cir.setReturnValue(false);
        }
    }
}
