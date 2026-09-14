package me.videogamesm12.w2k.toolbox.modules;

import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.data.TextOverlay;
import me.videogamesm12.w2k.kernel.data.Overlay;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.BooleanSetting;
import me.videogamesm12.w2k.kernel.module.setting.ColorSetting;
import net.kyori.adventure.text.Component;

import java.awt.*;
import java.util.Collections;

public class TargetHighlighter extends WModule
{
    public final BooleanSetting useCustomHighlightColor = register(new BooleanSetting("use_custom_highlight_color", "Use Custom Highlight Color", true));
    public final ColorSetting highlightColor = register(new ColorSetting("custom_highlight_color", "Custom Highlight Color", new Color(0, 0, 255)));

    public TargetHighlighter()
    {
        super("Target Highlighter",
                "Highlights the player that you are currently looking at.");

        addOverlay(new TextOverlay(0, 0, Overlay.Alignment.CENTER, Overlay.Alignment.CENTER,
                overlay -> isEnabled() && lookingAtValidTarget(),
                () -> Collections.singletonList(createTargetText()),
                System::currentTimeMillis,
                true));
    }

    public boolean lookingAtValidTarget(EntityInterface entity)
    {
        return versionAbstractionLayer().getTargetedEntity()
                .filter(target -> target.w2k$type().equalsIgnoreCase("minecraft:player"))
                .filter(target -> target.w2k$id() == entity.w2k$id())
                .isPresent();
    }

    public boolean lookingAtValidTarget()
    {
        return versionAbstractionLayer().getTargetedEntity()
                .filter(entity -> entity.w2k$type().equalsIgnoreCase("minecraft:player"))
                .isPresent();
    }

    private Component createTargetText()
    {
        return versionAbstractionLayer().getTargetedEntity()
                .filter(entity -> entity.w2k$type().equalsIgnoreCase("minecraft:player"))
                .map(entity -> Component.text("Target: " + entity.w2k$internalName()))
                .orElse(Component.empty());
    }
}
