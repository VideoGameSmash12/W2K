package me.videogamesm12.w2k.val.v26_1_x.graphics.renderer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.data.Overlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import java.awt.*;

@Getter
@RequiredArgsConstructor
public abstract class AbstractOverlayRenderer<T extends Overlay>
{
    private final Class<T> overlayClass;

    public abstract void renderOverlay(T overlay, GuiGraphicsExtractor context);

    public void text(GuiGraphicsExtractor context, Component text, int x, int y, boolean shadow)
    {
        context.text(Minecraft.getInstance().font, text, x, y, ARGB.white(255), shadow);
    }

    public void centeredText(GuiGraphicsExtractor context, Component text, int x, int y)
    {
        context.centeredText(Minecraft.getInstance().font, text, x, y, ARGB.white(255));
    }

    public void box(GuiGraphicsExtractor context, int x1, int y1, int x2, int y2, Color color)
    {
        context.fill(x1, y1, x2, y2, color(color));
    }

    public int color(Color color)
    {
        return ARGB.color(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }
}
