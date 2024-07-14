package me.videogamesm12.w2k.drivers.v1_14.mixin.injector;

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
    @Inject(method = "renderEntity(Lnet/minecraft/block/entity/BlockEntity;DDDFIZ)V", at = @At("HEAD"), cancellable = true)
    public void injectRenderEntity(BlockEntity blockEntity, double xOffset, double yOffset, double zOffset, float tickDelta, int blockBreakStage, boolean bl, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getRenderingSettings().isTileEntityRenderingDisabled()
                || Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled())
        {
            ci.cancel();
        }
    }
}
