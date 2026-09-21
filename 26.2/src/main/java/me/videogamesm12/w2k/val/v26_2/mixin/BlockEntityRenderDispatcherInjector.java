package me.videogamesm12.w2k.val.v26_2.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.render.BlockEntityRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherInjector
{
    @Unique
    private final BlockEntityRenderEvent blockEntityRenderEvent = new BlockEntityRenderEvent();

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    public <S extends BlockEntityRenderState> void injectRender(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, net.minecraft.client.renderer.state.level.CameraRenderState camera, CallbackInfo ci)
    {
        // TODO: Add workaround
        W2K.getEventBus().post(blockEntityRenderEvent.update(Objects.requireNonNull(Minecraft.getInstance().level).getBlockEntity(state.blockPos)));
        if (blockEntityRenderEvent.isCancelled())
        {
            ci.cancel();
        }
    }
}
