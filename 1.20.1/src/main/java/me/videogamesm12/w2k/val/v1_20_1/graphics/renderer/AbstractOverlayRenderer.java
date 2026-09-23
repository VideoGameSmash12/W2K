package me.videogamesm12.w2k.val.v1_20_1.graphics.renderer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.data.Overlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.awt.*;

@Getter
@RequiredArgsConstructor
public abstract class AbstractOverlayRenderer<T extends Overlay>
{
    private final Class<T> overlayClass;

    public abstract void renderOverlay(T overlay, DrawContext context);

    public void text(DrawContext context, Text text, int x, int y, boolean shadow)
    {
        context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFFFFFF, shadow);
    }

    public void centeredText(DrawContext context, Text text, int x, int y)
    {
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFFFFFF);
    }

    public void border(DrawContext context, int x, int y, int width, int height, Color color)
    {
        context.drawBorder(x, y, width, height, color(color));
    }

    public void box(DrawContext context, int x1, int y1, int x2, int y2, Color color)
    {
        context.fill(x1, y1, x2, y2, color(color));
    }

    public int color(Color color)
    {
        return ColorHelper.Argb.getArgb(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }
}
