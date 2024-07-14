package me.videogamesm12.w2k.drivers.v1_14.mixin.injector;

import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererMixin
{
    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFFZ)V", at = @At("HEAD"), cancellable = true)
    public void injectRenderEntity(Entity entity, double x, double y, double z, float yaw, float tickDelta, boolean forceHideHitbox, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getRenderingSettings().isEntityRenderingDisabled()
                || Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled())
        {
            ci.cancel();
        }
    }
}
