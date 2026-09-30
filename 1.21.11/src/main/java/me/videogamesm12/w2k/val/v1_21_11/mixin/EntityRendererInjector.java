package me.videogamesm12.w2k.val.v1_21_11.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.render.EntityGlowColorEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(EntityRenderer.class)
public class EntityRendererInjector
{
    @Unique
    private final EntityGlowColorEvent glowColorEvent = new EntityGlowColorEvent();

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    public void applyOverlayColor(Entity entity, EntityRenderState entityRenderState, float f, CallbackInfo ci)
    {
        if (MinecraftClient.getInstance().player == null)
        {
            return;
        }

        W2K.getEventBus().post(glowColorEvent.update((EntityInterface) entity));
        if (glowColorEvent.isCancelled() && !glowColorEvent.getColors().isEmpty())
        {
            final Color proposedReplacement = glowColorEvent.getColors().getFirst();
            entityRenderState.outlineColor = ColorHelper.getArgb(proposedReplacement.getRed(), proposedReplacement.getGreen(), proposedReplacement.getBlue(), proposedReplacement.getAlpha());
        }
    }
}
