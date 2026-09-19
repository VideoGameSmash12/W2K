package me.videogamesm12.w2k.val.v1_21_11.graphics;

import me.videogamesm12.w2k.kernel.abstraction.render.OverlayRenderDispatcher;
import me.videogamesm12.w2k.val.v1_21_11.graphics.renderer.AbstractOverlayRenderer;
import me.videogamesm12.w2k.val.v1_21_11.graphics.renderer.BoxOverlayRenderer;
import me.videogamesm12.w2k.val.v1_21_11.graphics.renderer.TextOverlayRenderer;

import java.util.HashMap;
import java.util.Map;

public class OverlayRenderDispatcherImpl implements OverlayRenderDispatcher<AbstractOverlayRenderer>
{
    public final Map<String, AbstractOverlayRenderer<?>> rendererMap = new HashMap<>();

    public OverlayRenderDispatcherImpl()
    {
        // Register our renderers
        registerRenderer("w2k:text", new TextOverlayRenderer());
        registerRenderer("w2k:box", new BoxOverlayRenderer());
    }

    @Override
    public <Renderer extends AbstractOverlayRenderer> void registerRenderer(String overlayId, Renderer renderer)
    {
        rendererMap.put(overlayId, renderer);
    }

    @Override
    public <Renderer extends AbstractOverlayRenderer> Renderer getRenderer(String overlayId)
    {
        return (Renderer) rendererMap.get(overlayId);
    }

    @Override
    public boolean isRendererRegistered(String overlayId)
    {
        return rendererMap.containsKey(overlayId);
    }
}
