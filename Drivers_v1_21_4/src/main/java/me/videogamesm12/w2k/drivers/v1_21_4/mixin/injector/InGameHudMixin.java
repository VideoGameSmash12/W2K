package me.videogamesm12.w2k.drivers.v1_21_4.mixin.injector;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IEntityEntry;
import me.videogamesm12.w2k.kernel.data.IItemStackEntry;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.toolbox.modules.BanHammer;
import me.videogamesm12.w2k.toolbox.modules.DevelopmentBuildWatermark;
import me.videogamesm12.w2k.toolbox.modules.TargetHighlighter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
    @Shadow
    private ItemStack currentStack;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Unique
    private final List<MutableText> watermarkText = DevelopmentBuildWatermark.createWatermarkText().stream()
            .map(component -> Text.Serialization.fromJsonTree(ComponentUtils.serializeComponent(component), BuiltinRegistries.createWrapperLookup()))
            .toList();

    @Inject(method = "renderMiscOverlays", at = @At(value = "TAIL"))
    public void renderTargetOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
    {
        renderTargetEntityOverlayIfEnabled(context);
    }

    @Unique
    public void renderTargetEntityOverlayIfEnabled(DrawContext context)
    {
        final BanHammer banHammer = W2K.getInstance().getModuleManager().getModule(BanHammer.class);
        final TargetHighlighter targetHighlighter = W2K.getInstance().getModuleManager().getModule(TargetHighlighter.class);
        if (((banHammer.isEnabled() && banHammer.isHammerActive(IItemStackEntry.class.cast(currentStack)) && banHammer.showOverlay.get()) || targetHighlighter.isEnabled())
                && client.targetedEntity != null)
        {
            final Entity target = client.targetedEntity;
            final IEntityEntry castedTarget = (IEntityEntry) target;

            if (castedTarget.w2k$type().equalsIgnoreCase("minecraft:player"))
            {
                context.drawCenteredTextWithShadow(getTextRenderer(), "Target: " + castedTarget.w2k$internalName(), context.getScaledWindowWidth() / 2, (context.getScaledWindowHeight() / 2) - 24, 0xFFFFFF);
            }
        }

        final DevelopmentBuildWatermark watermark = W2K.getInstance().getModuleManager().getModule(DevelopmentBuildWatermark.class);
        if (watermark.isEnabled())
        {
            for (int i = 0; i < watermarkText.size(); i++)
            {
                Text text = watermarkText.get(i);
                context.drawText(getTextRenderer(), watermarkText.get(i), (context.getScaledWindowWidth() - getTextRenderer().getWidth(text)) - 4, (i * getTextRenderer().fontHeight) + 4, 0xFFFFFF, true);
            }
        }
    }
}
