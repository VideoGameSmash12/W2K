package me.videogamesm12.w2k.val.v1_20_1.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.BlockEntityInterface;
import me.videogamesm12.w2k.kernel.event.render.BlockEntityRenderCheckEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherInjector
{
    @Unique
    private final BlockEntityRenderCheckEvent blockEntityRenderCheckEvent = new BlockEntityRenderCheckEvent();

    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"), cancellable = true)
    public void injectRenderEntity(BlockEntity blockEntity, float tickDelta, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, CallbackInfo ci)
    {
        W2K.getEventBus().post(blockEntityRenderCheckEvent.update((BlockEntityInterface) blockEntity, blockEntity));
        if (blockEntityRenderCheckEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    public void injectRenderEntity(BlockEntity blockEntity, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int light, int overlay, CallbackInfoReturnable<Boolean> cir)
    {
        W2K.getEventBus().post(blockEntityRenderCheckEvent.update((BlockEntityInterface) blockEntity, blockEntity));
        if (blockEntityRenderCheckEvent.isCancelled())
        {
            cir.setReturnValue(false);
        }
    }
}
