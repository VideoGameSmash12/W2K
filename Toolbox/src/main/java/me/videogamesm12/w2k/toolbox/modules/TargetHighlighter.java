package me.videogamesm12.w2k.toolbox.modules;

import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.BooleanSetting;
import me.videogamesm12.w2k.kernel.module.setting.ColorSetting;

import java.awt.*;

public class TargetHighlighter extends WModule
{
    public final BooleanSetting useCustomHighlightColor = register(new BooleanSetting("use_custom_highlight_color", "Use Custom Highlight Color", true));
    public final ColorSetting highlightColor = register(new ColorSetting("custom_highlight_color", "Custom Highlight Color", new Color(0, 0, 255)));

    public TargetHighlighter()
    {
        super("Target Highlighter",
                "Highlights the player that you are currently looking at.");
    }
}
