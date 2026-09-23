package me.videogamesm12.w2k.val.v1_21_11.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.render.BlockEntityRenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(BlockEntityRenderManager.class)
public class BlockEntityRenderManagerInjector
{
    @Unique
    private final BlockEntityRenderEvent blockEntityRenderEvent = new BlockEntityRenderEvent();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public <S extends BlockEntityRenderState> void injectRender(S blockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci)
    {
        // TODO: Add workaround
        W2K.getEventBus().post(blockEntityRenderEvent.update(Objects.requireNonNull(MinecraftClient.getInstance().world).getBlockEntity(blockEntityRenderState.pos)));
        if (blockEntityRenderEvent.isCancelled())
        {
            ci.cancel();
        }
    }
}
