package me.videogamesm12.w2k.kernel.abstraction.network;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.BaseVersionAbstractionLayer;
import me.videogamesm12.wcom.WPacket;

public abstract class AbstractPacketTranslator
{
    public abstract <T extends WPacket> void sendPacket(T packet);

    protected <Minecraft> BaseVersionAbstractionLayer<Minecraft> val()
    {
        return W2K.getInstance().getVersionAbstractionLayer();
    }
}
