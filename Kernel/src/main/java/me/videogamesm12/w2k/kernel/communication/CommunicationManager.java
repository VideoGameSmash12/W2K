package me.videogamesm12.w2k.kernel.communication;

import me.videogamesm12.wcom.Stage;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundConfigureAcknowledgePacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundHelloPacket;

public class CommunicationManager
{
    private int transactionId = -1;
    private Stage stage = Stage.HELLO;
    private WClientboundHelloPacket helloPacket = null;
    private WClientboundConfigureAcknowledgePacket configureAcknowledgePacket = null;
}
