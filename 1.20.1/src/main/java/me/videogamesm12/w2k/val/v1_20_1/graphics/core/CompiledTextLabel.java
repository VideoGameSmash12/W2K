package me.videogamesm12.w2k.val.v1_20_1.graphics.core;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.graphics.Alignment;
import me.videogamesm12.w2k.kernel.abstraction.graphics.CompiledDrawableObject;
import me.videogamesm12.w2k.kernel.graphics.core.TextLabel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class CompiledTextLabel implements CompiledDrawableObject<TextLabel>, Drawable
{
    private final TextLabel label;
    private Text compiledText;

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
            this.compiledText = (Text) W2K.getInstance().getVersionAbstractionLayer().text().adventureToNative(label.getSource());
            this.label.setWidth(MinecraftClient.getInstance().textRenderer.getWidth(compiledText));
            this.label.setHeight(MinecraftClient.getInstance().textRenderer.fontHeight);
            this.label.setSource(null);
        }
    }

    @Override
    public boolean shouldRegenerate()
    {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        final Text compiled = compiledText;

        final Alignment horizontal = label.horizontalAlignment();
        final Alignment vertical = label.verticalAlignment();

        int startingXPosition = Math.round(context.getScaledWindowWidth() * horizontal.getOffset());
        int startingYPosition = Math.round(context.getScaledWindowHeight() * vertical.getOffset());

        int x, y;

        switch (horizontal)
        {
            case LEAST -> x = startingXPosition + label.x();
            case CENTER -> x = startingXPosition + label.x() - Math.round((textRenderer.getWidth(compiled) * horizontal.getOffset()));
            case MOST -> x = startingXPosition - label.x() - textRenderer.getWidth(compiled);
            default -> x = 0;
        }
        switch (vertical)
        {
            case LEAST -> y = startingYPosition + label.y();
            case CENTER -> y = startingYPosition + label.y() - Math.round((textRenderer.fontHeight * horizontal.getOffset()));
            case MOST -> y = startingYPosition - label.y() - textRenderer.fontHeight;
            default -> y = 0;
        }
        context.drawTextWithShadow(textRenderer, compiled, x, y, ColorHelper.Argb.getArgb(255, 255, 255, 255));
    }
}
