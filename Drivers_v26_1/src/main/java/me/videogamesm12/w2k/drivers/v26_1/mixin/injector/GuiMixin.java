package me.videogamesm12.w2k.drivers.v26_1.mixin.injector;

import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IEntityEntry;
import me.videogamesm12.w2k.kernel.data.IItemStackEntry;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.toolbox.modules.BanHammer;
import me.videogamesm12.w2k.toolbox.modules.DevelopmentBuildWatermark;
import me.videogamesm12.w2k.toolbox.modules.TargetHighlighter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract Font getFont();

    @Unique
    private final List<Component> watermarkText = DevelopmentBuildWatermark.createWatermarkText().stream()
            .map(component -> ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, ComponentUtils.serializeComponent(component)).getOrThrow())
            .toList();

    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractCrosshair(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    public void renderTargetOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci)
    {
        extractTargetEntityOverlayIfEnabled(graphics);
    }

    @Unique
    public void extractTargetEntityOverlayIfEnabled(GuiGraphicsExtractor graphics)
    {
        final BanHammer banHammer = W2K.getInstance().getModuleManager().getModule(BanHammer.class);
        final TargetHighlighter targetHighlighter = W2K.getInstance().getModuleManager().getModule(TargetHighlighter.class);
        if (minecraft.player != null
                && ((banHammer.isEnabled() && banHammer.isHammerActive(IItemStackEntry.class.cast(minecraft.player.getInventory().getSelectedItem())) && banHammer.showOverlay.get()) || targetHighlighter.isEnabled())
                && minecraft.crosshairPickEntity != null)
        {
            final Entity target = minecraft.crosshairPickEntity;
            final IEntityEntry castedTarget = (IEntityEntry) target;

            if (castedTarget.w2k$type().equalsIgnoreCase("minecraft:player"))
            {
                graphics.centeredText(getFont(), "Target: " + castedTarget.w2k$internalName(), graphics.guiWidth() / 2, (graphics.guiHeight() / 2) - 24, ARGB.white(255));
            }
        }

        final DevelopmentBuildWatermark watermark = W2K.getInstance().getModuleManager().getModule(DevelopmentBuildWatermark.class);
        if (watermark.isEnabled())
        {
            for (int i = 0; i < watermarkText.size(); i++)
            {
                Component text = watermarkText.get(i);
                graphics.text(getFont(), watermarkText.get(i), (graphics.guiWidth() - getFont().width(text)) - 4, (i * getFont().lineHeight) + 4, ARGB.white(255), true);
            }
        }
    }
}
