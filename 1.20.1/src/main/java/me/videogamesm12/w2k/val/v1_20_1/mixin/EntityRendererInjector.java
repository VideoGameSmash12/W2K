package me.videogamesm12.w2k.val.v1_20_1.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.render.EntityRenderEvent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererInjector
{
    @Unique
    private final EntityRenderEvent event = new EntityRenderEvent();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void injectRenderEntity(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
        W2K.getEventBus().post(event.update((EntityInterface) entity));

        if (event.isCancelled())
        {
            ci.cancel();
        }
    }
}
