package me.videogamesm12.w2k.val.v26_1_x.graphics.renderer;

import me.videogamesm12.w2k.kernel.data.Overlay;
import me.videogamesm12.w2k.kernel.data.TextOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TextOverlayRenderer extends AbstractOverlayRenderer<TextOverlay>
{
    public TextOverlayRenderer()
    {
        super(TextOverlay.class);
    }

    @Override
    public void renderOverlay(TextOverlay overlay, GuiGraphicsExtractor context)
    {
        final Overlay.Alignment horizontal = overlay.getHorizontalAlignment();
        final Overlay.Alignment vertical = overlay.getVerticalAlignment();

        // Determine the starting points for rendering
        // LEAST, MOST would be something like XY (0, 480)
        // MOST, MOST would be something like XY (854, 480)
        // MOST, LEAST would be something like XY (854, 0)
        int baseX = switch (horizontal)
        {
            // Located at the very beginning at 0. We will need to add the X offset of the overlay to this.
            case LEAST -> 0;
            // Located at the end of the screen. We will need to subtract the X position of the overlay from this. An
            //  example of this would be X 854 on a window that is 854 pixels wide.
            case MOST -> context.guiWidth();
            // Located at the center of the screen. We will need to add the X offset of the overlay to this, but we can
            //  also subtract to it by just making the X offset negative. An example of this would be 427 on a window
            //  that is 854 pixels wide.
            case CENTER -> context.guiWidth() / 2;
        };
        int baseY = switch (vertical)
        {
            // Located at the very beginning at 0. We will need to add the Y offset of the overlay to this.
            case LEAST -> 0;
            // Located at the end of the screen. We will need to subtract the Y position of the overlay from this. An
            //  example of this would be Y 480 on a window that is 480 pixels tall.
            case MOST -> context.guiHeight();
            // Located at the center of the screen. We will need to add the Y offset of the overlay to this, but we can
            //  also subtract to it by just making the Y offset negative. An example of this would be 240 on a window
            //  that is 480 pixels tall.
            case CENTER -> context.guiHeight() / 2;
        };

        // Update if necessary
        if (overlay.shouldUpdate()) overlay.update();

        // Compared
        final List<Component> compiled = overlay.getCompiledText();

        // Ignore empty overlays
        if (compiled.isEmpty())
        {
            return;
        }

        /*int width = compiled.stream().map(MinecraftClient.getInstance().textRenderer::getWidth).max(Integer::compare).get();
        int height = compiled.size() * MinecraftClient.getInstance().textRenderer.fontHeight;
        //W2K.getLogger().info("Element Dimensions - Width {}, Height {}", width, height);

        // Box color
        final Color color = new Color(Math.abs(overlay.hashCode()));

        // Box offsets
        int x1 = baseX + horizontal.offset(overlay.getX());
        int y1 = baseY + vertical.offset(overlay.getY());
        int x2 = x1 + horizontal.offset(width);
        int y2 = y1 + vertical.offset(height);

        // Draw the box
        box(context, x1, y1, x2, y2, color);*/

        // Draw the text
        for (int i = 0; i < compiled.size(); i++)
        {
            textLine(overlay, context, compiled.get(i), baseX, baseY, i, horizontal, vertical);
        }
    }

    private void textLine(TextOverlay overlay, GuiGraphicsExtractor context, Component text, int baseX, int baseY, int level, Overlay.Alignment horizontal, Overlay.Alignment vertical)
    {
        final Font font = Minecraft.getInstance().font;
        int width = font.width(text);
        int height = font.lineHeight;
        int totalHeight = overlay.getCompiledText().size() * font.lineHeight;

        int x = horizontal != Overlay.Alignment.MOST ? (baseX + overlay.getX()) : baseX + horizontal.offset(width + overlay.getX());
        //int x = baseX + horizontal.offset(horizontal == Overlay.Alignment.LEAST ? overlay.getX() : width);
        /*int x = switch (horizontal)
        {
            case LEAST -> baseX + overlay.getX();
            case CENTER -> baseX + overlay.getX();
            case MOST -> baseX + horizontal.offset(width);
        };*/
        //int y = baseY + vertical.offset(overlay.getY()) + vertical.offset((level + 1) * height);
        // 240 - 0 - (18 - )
        //int y = baseY + vertical.offset(overlay.getY()) + vertical.offset(totalHeight + vertical.offset((level) * height));
        int y = baseY + vertical.offset(overlay.getY()) + vertical.offset(switch (vertical)
        {
            case LEAST -> (level) * height;
            case CENTER -> ((level) * height) * 2;
            case MOST -> totalHeight - ((level) * height);
        });

        if (horizontal != Overlay.Alignment.CENTER)
            text(context, text, x, y, overlay.isShadowEnabled());
        else
            centeredText(context, text, x, y);
    }
}
