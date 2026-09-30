package me.videogamesm12.w2k.val.v26_1_x.graphics.core;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.graphics.CompiledDrawableObject;
import me.videogamesm12.w2k.kernel.graphics.Alignment;
import me.videogamesm12.w2k.kernel.graphics.core.TextLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class CompiledTextLabel implements CompiledDrawableObject<TextLabel>, Renderable
{
    private final TextLabel label;
    private Component compiledText;

    public CompiledTextLabel(final TextLabel label)
    {
        this.label = label;
        update();
    }

    @Override
    public TextLabel source()
    {
        return label;
    }

    @Override
    public boolean shouldUpdate()
    {
        return label.getSource() != null;
    }

    @Override
    public void update()
    {
        if (label.getSource() != null)
        {
            this.compiledText = (Component) W2K.getInstance().getVersionAbstractionLayer().text().adventureToNative(label.getSource());
            this.label.setWidth(Minecraft.getInstance().font.width(compiledText));
            this.label.setHeight(Minecraft.getInstance().font.lineHeight);
            this.label.setSource(null);
        }
    }

    @Override
    public boolean shouldRegenerate()
    {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta)
    {
        final Font font = Minecraft.getInstance().font;
        final Component compiled = compiledText;

        final Alignment horizontal = label.horizontalAnchor();
        final Alignment vertical = label.verticalAnchor();

        int startingXPosition = Math.round(context.guiWidth() * horizontal.getOffset());
        int startingYPosition = Math.round(context.guiHeight() * vertical.getOffset());

        int x, y;

        switch (horizontal)
        {
            case LEAST -> x = startingXPosition + label.x();
            case CENTER -> x = startingXPosition + label.x() - Math.round((font.width(compiled) * horizontal.getOffset()));
            case MOST -> x = startingXPosition - label.x() - font.width(compiled);
            default -> x = 0;
        }
        switch (vertical)
        {
            case LEAST -> y = startingYPosition + label.y();
            case CENTER -> y = startingYPosition + label.y() - Math.round((font.lineHeight * horizontal.getOffset()));
            case MOST -> y = startingYPosition - label.y() - font.lineHeight;
            default -> y = 0;
        }
        context.text(font, compiled, x, y, ARGB.color(255, 255, 255, 255));
    }
}
