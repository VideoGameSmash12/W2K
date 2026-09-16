package me.videogamesm12.w2k.val.v1_20_1.graphics;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.render.OverlayRenderDispatcher;
import me.videogamesm12.w2k.kernel.data.Overlay;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.val.v1_20_1.graphics.renderer.AbstractOverlayRenderer;
import me.videogamesm12.w2k.val.v1_20_1.graphics.renderer.BoxOverlayRenderer;
import me.videogamesm12.w2k.val.v1_20_1.graphics.renderer.TextOverlayRenderer;
import me.videogamesm12.w2k.val.v1_20_1.graphics.renderer.TextOverlayRenderer2;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OverlayRenderDispatcherImpl implements OverlayRenderDispatcher<AbstractOverlayRenderer>
{
    public final Map<String, AbstractOverlayRenderer<?>> rendererMap = new HashMap<>();

    public OverlayRenderDispatcherImpl()
    {
        // Register our renderers
        //registerRenderer("w2k:text", new TextOverlayRenderer());
        registerRenderer("w2k:text", new TextOverlayRenderer2());
        registerRenderer("w2k:box", new BoxOverlayRenderer());

        // Register our overlay renderer
        /*HudRenderCallback.EVENT.register((lol, ass) ->
        {
            W2K.getInstance().getModuleManager().getIdRegistry().values().stream()
                    .filter(WModule::isEnabled)
                    .map(WModule::getOverlays)
                    .flatMap(Collection::stream)
                    .filter(overlay -> overlay.getShouldDisplay().test(overlay))
                    .forEach(overlay ->
                    {
                        if (getRenderer(overlay.getId()) != null)
                            getRenderer(overlay.getId()).renderOverlay(overlay, lol);
                    });
        });*/
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
}
