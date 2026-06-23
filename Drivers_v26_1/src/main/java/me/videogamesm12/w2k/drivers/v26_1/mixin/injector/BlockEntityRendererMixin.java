package me.videogamesm12.w2k.drivers.v26_1.mixin.injector;

import com.mojang.blaze3d.vertex.PoseStack;
import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRendererMixin
{
    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    public <S extends BlockEntityRenderState> void injectRenderEntity(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getRenderingSettings().isTileEntityRenderingDisabled()
                || Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled())
        {
            ci.cancel();
        }
    }
}
