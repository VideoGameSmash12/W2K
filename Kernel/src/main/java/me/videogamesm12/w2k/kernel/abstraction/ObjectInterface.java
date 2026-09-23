package me.videogamesm12.w2k.kernel.abstraction;

import me.videogamesm12.w2k.kernel.W2K;

/**
 * <h1>ObjectInterface</h1>
 * <p>The root level object wrapper which adds a utility method to access the Version Abstraction Layer. All wrappers
 *  extend from this wrapper.</p>
 */
public interface ObjectInterface
{
    /**
     * Returns the Version Abstraction Layer.
     * @return              {@link BaseVersionAbstractionLayer}
     * @param <Minecraft>   {@code MinecraftClient} or {@code Minecraft} (depending on your mappings)
     */
    default <Minecraft> BaseVersionAbstractionLayer<Minecraft> w2k$val()
    {
        return W2K.getInstance().getVersionAbstractionLayer();
    }
}
