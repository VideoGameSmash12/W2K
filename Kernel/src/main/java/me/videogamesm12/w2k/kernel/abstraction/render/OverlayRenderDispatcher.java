package me.videogamesm12.w2k.kernel.abstraction.render;

public interface OverlayRenderDispatcher<BaseRenderer>
{
    <Renderer extends BaseRenderer> void registerRenderer(String overlayId, Renderer renderer);

    <Renderer extends BaseRenderer> Renderer getRenderer(String overlayId);

    boolean isRendererRegistered(String overlayId);
}
