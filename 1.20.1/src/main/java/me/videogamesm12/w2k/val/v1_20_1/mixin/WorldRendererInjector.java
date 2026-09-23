package me.videogamesm12.w2k.val.v1_20_1.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.render.EntityGlowColorEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(WorldRenderer.class)
public class WorldRendererInjector
{
    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private final EntityGlowColorEvent glowColorEvent = new EntityGlowColorEvent();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;setColor(IIII)V", shift = At.Shift.AFTER))
    public void applyOverlayColor(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci, @Local Entity entity, @Local OutlineVertexConsumerProvider provider)
    {
        if (client.player == null)
        {
            return;
        }

        W2K.getEventBus().post(glowColorEvent.update((EntityInterface) entity));
        if (glowColorEvent.isCancelled() && !glowColorEvent.getColors().isEmpty())
        {
            final Color proposedReplacement = glowColorEvent.getColors().get(0);
            provider.setColor(proposedReplacement.getRed(), proposedReplacement.getGreen(), proposedReplacement.getBlue(), proposedReplacement.getAlpha());
        }
    }
}
