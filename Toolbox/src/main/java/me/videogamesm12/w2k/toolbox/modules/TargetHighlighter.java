package me.videogamesm12.w2k.toolbox.modules;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.entity.TargetEntityUpdateEvent;
import me.videogamesm12.w2k.kernel.event.render.EntityGlowCheckEvent;
import me.videogamesm12.w2k.kernel.event.render.EntityGlowColorEvent;
import me.videogamesm12.w2k.kernel.event.render.OverlayRequestEvent;
import me.videogamesm12.w2k.kernel.graphics.Alignment;
import me.videogamesm12.w2k.kernel.graphics.core.TextLabel;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.BooleanSetting;
import me.videogamesm12.w2k.kernel.module.setting.ColorSetting;
import net.kyori.adventure.text.Component;

import java.awt.*;

public class TargetHighlighter extends WModule
{
    public final BooleanSetting useCustomHighlightColor = register(new BooleanSetting("use_custom_highlight_color", "Use Custom Highlight Color", true));
    public final ColorSetting highlightColor = register(new ColorSetting("custom_highlight_color", "Custom Highlight Color", new Color(0, 0, 255)));
    //--
    private final TextLabel label = addDrawableOverlay(new TextLabel(Component.empty(), 0, 32, Alignment.CENTER, Alignment.CENTER));

    public TargetHighlighter()
    {
        super("Target Highlighter",
                "Highlights the player that you are currently looking at.");
    }

    @Subscribe
    public void onTargetEntityUpdate(TargetEntityUpdateEvent event)
    {
        label.setSource(createTargetText(event.getTarget()));
    }

    @Subscribe
    public void onEntityGlowCheck(EntityGlowCheckEvent event)
    {
        if (!lookingAtValidTarget(event.getEntity()))
        {
            return;
        }

        event.setOutcome(true);
    }

    @Subscribe
    public void onEntityGlowColor(EntityGlowColorEvent event)
    {
        if (event.isCancelled()
                || !event.getEntity().equals(versionAbstractionLayer().getTargetedEntityUnsafe())
                || !useCustomHighlightColor.get()
                || !lookingAtValidTarget(event.getEntity()))
        {
            return;
        }

        event.addColor(highlightColor.get());
        event.setCancelled(true);
    }

    @Subscribe
    public void onOverlayRequest(OverlayRequestEvent event)
    {
        if (lookingAtValidTarget())
            event.submit(label);
    }

    public boolean lookingAtValidTarget(EntityInterface entity)
    {
        final EntityInterface target = versionAbstractionLayer().getTargetedEntityUnsafe();
        return target != null
                && target.w2k$type().equalsIgnoreCase("minecraft:player")
                && target.w2k$uuid().equals(entity.w2k$uuid());
    }

    public boolean lookingAtValidTarget()
    {
        final EntityInterface entity = versionAbstractionLayer().getTargetedEntityUnsafe();
        return entity != null && entity.w2k$type().equalsIgnoreCase("minecraft:player");
    }

    private Component createTargetText(final EntityInterface entity)
    {
        if (entity == null
                || !entity.w2k$type().equalsIgnoreCase("minecraft:player"))
        {
            return Component.empty();
        }

        return Component.text("Target: " + entity.w2k$internalName());
    }
}
