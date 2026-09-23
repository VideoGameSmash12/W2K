package me.videogamesm12.w2k.kernel.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.BaseVersionAbstractionLayer;

import java.util.function.Function;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public abstract class Overlay
{
    private final String id;
    private final int x;
    private final int y;
    private final Alignment horizontalAlignment;
    private final Alignment verticalAlignment;
    private final Predicate<Overlay> shouldDisplay;

    @RequiredArgsConstructor
    public enum Alignment
    {
        LEAST(value -> value),
        CENTER(value -> value / 2),
        MOST(value -> -value);

        private final Function<Integer, Integer> offsetter;

        public int offset(int value)
        {
            return offsetter.apply(value);
        }
    }

    protected final <Minecraft> BaseVersionAbstractionLayer<Minecraft> val()
    {
        return W2K.getInstance().getVersionAbstractionLayer();
    }
}
