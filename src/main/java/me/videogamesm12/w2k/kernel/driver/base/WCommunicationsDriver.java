package me.videogamesm12.w2k.kernel.driver.base;

import me.videogamesm12.w2k.kernel.data.packet.WPacket;

import java.util.function.Consumer;

public interface WCommunicationsDriver extends WDriver
{
    void sendPacket(final WPacket packet);

    void sendTransactionalPacket(final WPacket packet, final Consumer<? extends WPacket> consumer);

    <T extends WPacket> void receivePacket(final T packet);
}
