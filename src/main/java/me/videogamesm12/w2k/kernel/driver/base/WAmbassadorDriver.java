package me.videogamesm12.w2k.kernel.driver.base;

import me.videogamesm12.w2k.kernel.protocol.Stage;
import me.videogamesm12.w2k.kernel.protocol.WPacket;

public interface WAmbassadorDriver extends WDriver
{
    <T extends WPacket> void receivePacket(String id, T packet);

    <T extends WPacket> void sendPacket(T packet);

    Stage getStage();

    int nextTransactionId();
}
