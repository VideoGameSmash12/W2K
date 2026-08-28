package me.videogamesm12.w2k.drivers.v26_2.mixin.injector;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IItemStackEntry;
import me.videogamesm12.w2k.toolbox.modules.BanHammer;
import me.videogamesm12.w2k.toolbox.modules.TargetHighlighter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin
{
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    public <T extends Entity, S extends EntityRenderState> void applyCustomGlowTexture(T entity, S state, float partialTicks, CallbackInfo ci)
    {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().crosshairPickEntity != entity)
        {
            return;
        }

        Color proposedReplacement = null;

        final TargetHighlighter targetHighlighter = W2K.getInstance().getModuleManager().getModule(TargetHighlighter.class);
        if (targetHighlighter.isEnabled()
                && targetHighlighter.useCustomHighlightColor.get())
        {
            proposedReplacement = targetHighlighter.highlightColor.get();
        }

        // Ban Hammer takes priority over Target Highlighter
        final BanHammer banHammer = W2K.getInstance().getModuleManager().getModule(BanHammer.class);
        if (banHammer.isEnabled()
                && banHammer.useCustomHighlightColor.get()
                && banHammer.outlineTarget.get()
                && banHammer.isHammerActive(IItemStackEntry.class.cast(Minecraft.getInstance().player.getInventory().getSelectedItem())))
        {
            proposedReplacement = banHammer.highlightColor.get();
        }

        if (proposedReplacement != null)
        {
            state.outlineColor = ARGB.color(proposedReplacement.getAlpha(), proposedReplacement.getRed(), proposedReplacement.getGreen(), proposedReplacement.getBlue());
        }
    }
}
