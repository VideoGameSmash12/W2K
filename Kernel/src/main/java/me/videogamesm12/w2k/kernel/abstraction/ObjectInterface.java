package me.videogamesm12.w2k.kernel.abstraction;

import me.videogamesm12.w2k.kernel.W2K;

public interface ObjectInterface
{
    default <Minecraft> BaseVersionAbstractionLayer<Minecraft> w2k$val()
    {
        return W2K.getInstance().getVersionAbstractionLayer();
    }
}
