package me.videogamesm12.w2k.drivers.v1_8.mixin.injector;

import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRendererMixin
{
    @Inject(method = "renderEntity(Lnet/minecraft/block/entity/BlockEntity;DDDFI)V", at = @At("HEAD"), cancellable = true)
    public void injectRenderEntity(BlockEntity entity, double x, double y, double z, float tickDelta, int destroyProgress, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getRenderingSettings().isTileEntityRenderingDisabled()
                || Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled())
        {
            ci.cancel();
        }
    }
}
