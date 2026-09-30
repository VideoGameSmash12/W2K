package me.videogamesm12.w2k.val.v1_21_11.graphics;

import me.videogamesm12.w2k.kernel.abstraction.graphics.AbstractGraphicsHandler;
import me.videogamesm12.w2k.kernel.graphics.core.Box;
import me.videogamesm12.w2k.kernel.graphics.core.TextLabel;
import me.videogamesm12.w2k.val.v1_21_11.graphics.core.CompiledBox;
import me.videogamesm12.w2k.val.v1_21_11.graphics.core.CompiledTextLabel;

public class GraphicsHandler extends AbstractGraphicsHandler
{
    @Override
    public void registerCoreCompilers()
    {
        registerCompiler(TextLabel.class, (label) ->
        {
            if (!(label.compiled() instanceof CompiledTextLabel))
            {
                label.compiled(new CompiledTextLabel(label));
            }

            return label.compiled();
        });
        registerCompiler(Box.class, (box) ->
        {
            if (!(box.compiled() instanceof CompiledBox))
            {
                box.compiled(new CompiledBox(box));
            }

            return box.compiled();
        });
    }
}
