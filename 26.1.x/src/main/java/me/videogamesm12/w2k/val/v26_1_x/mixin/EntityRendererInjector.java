package me.videogamesm12.w2k.val.v26_1_x.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.render.EntityGlowColorEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
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

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    public <T extends Entity, S extends EntityRenderState> void applyOverlayColor(T entity, S state, float partialTicks, CallbackInfo ci)
    {
        if (Minecraft.getInstance().player == null)
        {
            return;
        }

        W2K.getEventBus().post(glowColorEvent.update((EntityInterface) entity));
        if (glowColorEvent.isCancelled() && !glowColorEvent.getColors().isEmpty())
        {
            final Color proposedReplacement = glowColorEvent.getColors().getFirst();
            state.outlineColor = ARGB.color(proposedReplacement.getRed(), proposedReplacement.getGreen(), proposedReplacement.getBlue(), proposedReplacement.getAlpha());
        }
    }
}
