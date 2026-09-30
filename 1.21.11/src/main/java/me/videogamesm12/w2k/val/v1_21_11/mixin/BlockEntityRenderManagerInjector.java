package me.videogamesm12.w2k.val.v1_21_11.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.BlockEntityInterface;
import me.videogamesm12.w2k.kernel.event.render.BlockEntityRenderCheckEvent;
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
    private final BlockEntityRenderCheckEvent blockEntityRenderCheckEvent = new BlockEntityRenderCheckEvent();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public <S extends BlockEntityRenderState> void injectRender(S blockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci)
    {
        // TODO: Add workaround
        W2K.getEventBus().post(blockEntityRenderCheckEvent.update((BlockEntityInterface) Objects.requireNonNull(MinecraftClient.getInstance().world).getBlockEntity(blockEntityRenderState.pos), blockEntityRenderState));
        if (blockEntityRenderCheckEvent.isCancelled())
        {
            ci.cancel();
        }
    }
}
