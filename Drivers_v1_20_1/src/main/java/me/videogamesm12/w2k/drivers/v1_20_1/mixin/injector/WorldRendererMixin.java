package me.videogamesm12.w2k.drivers.v1_20_1.mixin.injector;

import com.llamalad7.mixinextras.sugar.Local;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IItemStackEntry;
import me.videogamesm12.w2k.toolbox.modules.BanHammer;
import me.videogamesm12.w2k.toolbox.modules.TargetHighlighter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin
{
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;setColor(IIII)V", shift = At.Shift.AFTER))
    public void applyOverlayColor(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci, @Local Entity entity, @Local OutlineVertexConsumerProvider provider)
    {
        if (client.player == null || client.targetedEntity != entity)
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
                && banHammer.isHammerActive(IItemStackEntry.class.cast(client.player.getInventory().getMainHandStack()))
                && client.targetedEntity == entity)
        {
            proposedReplacement = banHammer.highlightColor.get();
        }

        if (proposedReplacement != null)
        {
            provider.setColor(proposedReplacement.getRed(), proposedReplacement.getGreen(), proposedReplacement.getBlue(), proposedReplacement.getAlpha());
        }
    }
}
