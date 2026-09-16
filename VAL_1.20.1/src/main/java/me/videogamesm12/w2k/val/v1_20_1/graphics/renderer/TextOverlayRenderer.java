package me.videogamesm12.w2k.val.v1_20_1.graphics.renderer;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.TextOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

import java.util.Comparator;

public class TextOverlayRenderer extends AbstractOverlayRenderer<TextOverlay>
{
    public TextOverlayRenderer()
    {
        super(TextOverlay.class);
    }

    @Override
    public void renderOverlay(TextOverlay overlay, DrawContext context)
    {
        // Determine the starting points for rendering
        // LEAST, MOST would be something like XY (0, 480)
        // MOST, MOST would be something like XY (854, 480)
        // MOST, LEAST would be something like XY (854, 0)
        int baseX = switch (overlay.getHorizontalAlignment())
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
        int baseY = switch (overlay.getVerticalAlignment())
        {
            // Located at the very beginning at 0. We will need to add the Y offset of the overlay to this.
            case LEAST -> 0;
            // Located at the end of the screen. We will need to subtract the Y position of the overlay from this. An
            //  example of this would be Y 480 on a window that is 480 pixels tall.
            case MOST -> context.getScaledWindowWidth();
            // Located at the center of the screen. We will need to add the Y offset of the overlay to this, but we can
            //  also subtract to it by just making the Y offset negative. An example of this would be 240 on a window
            //  that is 480 pixels tall.
            case CENTER -> context.getScaledWindowWidth() / 2;
        };

        // Update the overlay if necessary
        if (overlay.shouldUpdate()) overlay.update();

        // Get the text renderer
        final TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

        // Calculate the total width of the actual overlay by determining the width of the longest message in the text
        //  overlay
        int overlayWidth = overlay.getCompiledText().stream().map(object -> (Text) object)
                .sorted(Comparator.comparingInt(renderer::getWidth))
                .map(renderer::getWidth)
                .findFirst()
                .orElse(0);
        int overlayHeight = overlay.getCompiledText().size() * renderer.fontHeight;

        W2K.getLogger().info("Debug - Overlay Width {}", overlayWidth);
        W2K.getLogger().info("Debug - Overlay Height {}", overlayHeight);

        // Example numbers for the sake of readability - Width of the dev build overlay would be 276
        // For LEAST, 0 + 4 (overlay X offset) = 4
        // For MOST, 854 (base X) - 276 (overlay width) - 4 (overlay X offset) = 574
        // For CENTER, 427 (base X) - 138 (overlay width / 2) + 4 (overlay X offset) = 289
        int renderX = switch (overlay.getHorizontalAlignment())
        {
            case LEAST -> baseX + overlay.getX();
            case MOST -> baseX - overlayWidth - overlay.getX();
            case CENTER -> baseX - (overlayWidth / 2) + overlay.getX();
        };
        // Example numbers for the sake of readability - Height of the dev build overlay would be 86
        // For LEAST, 0 + 4 (overlay Y offset) = 4
        // For MOST, 480 (base Y) - 18 (overlay height) - 4 (overlay Y offset) = 458
        // For CENTER, 240 (base Y) - 18 (overlay height) + 4 (overlay Y offset) = 226
        int renderY = switch (overlay.getVerticalAlignment())
        {
            case LEAST -> baseY + overlay.getY();
            case MOST -> baseY - overlayHeight - overlay.getY();
            case CENTER -> baseY - overlayHeight + overlay.getY();
        };

        // Debugging overlay
        W2K.getLogger().info("Debug - Filling XY ({}, {}) to XY ({}, {})", baseX, baseY, renderX, renderY);
        context.fill(baseX, baseY, renderX, renderY, 0xFFFFFF);

        /*int overlayXOffset = overlay.getX();
        int overlayYOffset = overlay.getY();*/


    }
}
