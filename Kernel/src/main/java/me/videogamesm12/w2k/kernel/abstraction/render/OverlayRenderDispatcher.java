package me.videogamesm12.w2k.kernel.abstraction.render;

import me.videogamesm12.w2k.kernel.data.Overlay;

public interface OverlayRenderDispatcher<BaseRenderer>
{
    <Renderer extends BaseRenderer> void registerRenderer(String overlayId, Renderer renderer);

    <Renderer extends BaseRenderer> Renderer getRenderer(String overlayId);
}
