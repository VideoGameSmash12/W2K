package me.videogamesm12.w2k.val.v1_21_11.graphics.renderer;

import me.videogamesm12.w2k.kernel.data.BoxOverlay;
import me.videogamesm12.w2k.kernel.data.Overlay;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class BoxOverlayRenderer extends AbstractOverlayRenderer<BoxOverlay>
{
    public BoxOverlayRenderer()
    {
        super(BoxOverlay.class);
    }

    @Override
    public void renderOverlay(BoxOverlay overlay, DrawContext context)
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
            case MOST -> context.getScaledWindowWidth();
            // Located at the center of the screen. We will need to add the X offset of the overlay to this, but we can
            //  also subtract to it by just making the X offset negative. An example of this would be 427 on a window
            //  that is 854 pixels wide.
            case CENTER -> context.getScaledWindowWidth() / 2;
        };
        int baseY = switch (vertical)
        {
            // Located at the very beginning at 0. We will need to add the Y offset of the overlay to this.
            case LEAST -> 0;
            // Located at the end of the screen. We will need to subtract the Y position of the overlay from this. An
            //  example of this would be Y 480 on a window that is 480 pixels tall.
            case MOST -> context.getScaledWindowHeight();
            // Located at the center of the screen. We will need to add the Y offset of the overlay to this, but we can
            //  also subtract to it by just making the Y offset negative. An example of this would be 240 on a window
            //  that is 480 pixels tall.
            case CENTER -> context.getScaledWindowHeight() / 2;
        };

        // Box color
        final Color color = overlay.getColor();

        // Box offsets
        int x1 = baseX + horizontal.offset(overlay.getX());
        int y1 = baseY + vertical.offset(overlay.getY());
        int x2 = x1 + horizontal.offset(overlay.getWidth());
        int y2 = y1 + vertical.offset(overlay.getHeight());

        // Draw the box
        box(context, x1, y1, x2, y2, color);
    }
}
