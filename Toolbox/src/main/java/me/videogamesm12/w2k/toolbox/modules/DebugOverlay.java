package me.videogamesm12.w2k.toolbox.modules;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.kernel.event.render.OverlayRequestEvent;
import me.videogamesm12.w2k.kernel.graphics.Alignment;
import me.videogamesm12.w2k.kernel.graphics.core.TextLabel;
import me.videogamesm12.w2k.kernel.module.WModule;
import net.kyori.adventure.text.Component;

public class DebugOverlay extends WModule
{
    private final TextLabel overlayTopLeft = addDrawableOverlay(new TextLabel(Component.text("TOP_LEFT"), 0, 0));
    private final TextLabel overlayTopCenter = addDrawableOverlay(new TextLabel(Component.text("TOP_CENTER"), 0, 0, Alignment.CENTER, Alignment.LEAST));
    private final TextLabel overlayTopRight = addDrawableOverlay(new TextLabel(Component.text("TOP_RIGHT"), 0, 0, Alignment.MOST, Alignment.LEAST));
    private final TextLabel overlayCenterLeft = addDrawableOverlay(new TextLabel(Component.text("CENTER_LEFT"), 0, 0, Alignment.LEAST, Alignment.CENTER));
    private final TextLabel overlayDeadCenter = addDrawableOverlay(new TextLabel(Component.text("CENTER"), 0, 0, Alignment.CENTER, Alignment.CENTER));
    private final TextLabel overlayCenterRight = addDrawableOverlay(new TextLabel(Component.text("CENTER_RIGHT"), 0, 0, Alignment.MOST, Alignment.CENTER));
    private final TextLabel overlayBottomLeft = addDrawableOverlay(new TextLabel(Component.text("BOTTOM_LEFT"), 0, 0, Alignment.LEAST, Alignment.MOST));
    private final TextLabel overlayBottomCenter = addDrawableOverlay(new TextLabel(Component.text("BOTTOM_CENTER"), 0, 0, Alignment.CENTER, Alignment.MOST));
    private final TextLabel overlayBottomRight = addDrawableOverlay(new TextLabel(Component.text("BOTTOM_RIGHT"), 0, 0, Alignment.MOST, Alignment.MOST));

    public DebugOverlay()
    {
        super("Debug Overlay", "Simple module used to debug overlays.");
    }

    @Subscribe
    public void onOverlayRequest(OverlayRequestEvent event)
    {
        event.submitAll(getDrawableOverlays());
    }
}
