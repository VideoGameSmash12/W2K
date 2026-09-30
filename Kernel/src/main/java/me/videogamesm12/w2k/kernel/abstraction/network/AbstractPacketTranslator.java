package me.videogamesm12.w2k.kernel.abstraction.network;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.BaseVersionAbstractionLayer;
import me.videogamesm12.wcom.WPacket;

/**
 * <h1>AbstractPacketTranslator</h1>
 * <p>A class responsible for the translation and dispatching of packets under the WCom umbrella to and from Minecraft's
 *  built-in plugin messaging (also known as custom payload) system.</p>
 */
public abstract class AbstractPacketTranslator
{
    /**
     * Converts a packet to Minecraft's native custom payload system and dispatches it using the relevant custom payload
     *  packet type.
     * @param packet    An implementation of {@link WPacket}
     * @param <T>       An implementation of {@link WPacket}
     */
    public abstract <T extends WPacket> void sendPacket(T packet);

    /**
     * Gets the Version Abstraction Layer.
     * @return              {@link BaseVersionAbstractionLayer}
     * @param <Minecraft>   {@code MinecraftClient} or {@code Minecraft} (depending on your mappings)
     */
    protected <Minecraft> BaseVersionAbstractionLayer<Minecraft> val()
    {
        return W2K.getInstance().getVersionAbstractionLayer();
    }
}
