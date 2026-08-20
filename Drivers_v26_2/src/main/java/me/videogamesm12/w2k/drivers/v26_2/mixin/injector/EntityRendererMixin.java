package me.videogamesm12.w2k.drivers.v26_2.mixin.injector;

import com.mojang.blaze3d.vertex.PoseStack;
import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererMixin
{
    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    public <E extends Entity, S extends EntityRenderState> void injectRenderEntity(S renderState, CameraRenderState camera, double x, double y, double z, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getRenderingSettings().isEntityRenderingDisabled()
                || Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled())
        {
            ci.cancel();
        }
    }
}
