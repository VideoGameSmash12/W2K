package me.videogamesm12.w2k.kernel.communication;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.network.AbstractPacketTranslator;
import me.videogamesm12.w2k.kernel.event.network.DisconnectEvent;
import me.videogamesm12.w2k.kernel.event.network.RegisterPluginMessageEvent;
import me.videogamesm12.w2k.kernel.event.protocol.StateChangeEvent;
import me.videogamesm12.wcom.Stage;
import me.videogamesm12.wcom.WPacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundConfigureAcknowledgePacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundHelloPacket;
import me.videogamesm12.wcom.protocol.common.WCommonErrorPacket;
import me.videogamesm12.wcom.protocol.serverbound.WServerboundConfigurePacket;
import me.videogamesm12.wcom.protocol.serverbound.WServerboundHelloPacket;
import net.kyori.adventure.nbt.CompoundBinaryTag;

@Getter
public class WCommunicationManager
{
    private final EventBus eventBus = new EventBus("w2k:communication_manager");
    private final AbstractPacketTranslator packetTranslator = W2K.getInstance().getVersionAbstractionLayer().packetTranslator();

    private int transactionId = -1;
    private Stage stage = Stage.HELLO;
    private WClientboundHelloPacket helloPacket = null;
    private WClientboundConfigureAcknowledgePacket configureAcknowledgePacket = null;

    public WCommunicationManager()
    {
        W2K.getEventBus().register(this);
    }

    public void handlePacket(final WPacket packet)
    {
        if (stage == Stage.ERROR)
        {
            return;
        }

        // Terminate communication if the packet is for a mismatched stage
        if (packet.getPacketMeta().stage() != Stage.ANY && packet.getPacketMeta().stage() != stage)
        {
            final Stage packetStage = packet.getPacketMeta().stage();
            W2K.getLogger().warn("Received mismatched packet stage from server (expected {}, got {})", stage, packetStage);
            sendPacket(new WCommonErrorPacket(packet.getTransactionId(), WCommonErrorPacket.Error.INVALID_STAGE, "Packet is for stage " + packetStage + ", but I am on stage " + stage, true));
            setStage(Stage.ERROR);
            return;
        }

        Preconditions.checkNotNull(packet, "Received null packet");

        // Terminate any future handling if the packet is for an irrecoverable error
        if (packet instanceof WCommonErrorPacket && ((WCommonErrorPacket) packet).isTerminationWorthy())
        {
            final WCommonErrorPacket error = (WCommonErrorPacket) packet;
            W2K.getLogger().warn("Received unrecoverable, non-negotiable error from server, entering \"error\" state -> {}: {}", error.getError(), error.getMessage());

            // Update our stage
            setStage(Stage.ERROR);

            // Manually post this event as some modules may want to know if something is wrong
            eventBus.post(packet);
            return;
        }

        switch (stage)
        {
            case HELLO:
            {
                // An error occurred from the server side. Enter error state as this is likely a blunt refusal from the server
                if (packet instanceof WCommonErrorPacket)
                {
                    W2K.getLogger().warn("Server replied with an error during the initial handshake, entering \"error\" state -> {}",
                            ((WCommonErrorPacket) packet).getError().toString());
                    setStage(Stage.ERROR);
                    return;
                }
                // Server-to-client hello packet
                else if (packet instanceof WClientboundHelloPacket)
                {
                    // Already received a hello packet?
                    if (helloPacket != null)
                    {
                        W2K.getLogger().warn("Already received valid hello packet from server. Entering error state");
                        sendPacket(new WCommonErrorPacket(packet.getTransactionId(), WCommonErrorPacket.Error.ILLEGAL_REQUEST, "I have already received a hello packet!", true));
                        setStage(Stage.ERROR);
                        return;
                    }

                    // Set the hello packet so we know it has already been sent
                    helloPacket = (WClientboundHelloPacket) packet;
                    if (helloPacket.getProtocolVersion() != WPacket.protocolVersion)
                    {
                        W2K.getLogger().warn("Server is using the wrong version of the protocol. Entering error state");
                        sendPacket(new WCommonErrorPacket(packet.getTransactionId(), WCommonErrorPacket.Error.UNSUPPORTED_W2K_VERSION, "I can only handle protocol version " + WPacket.protocolVersion, true));
                        setStage(Stage.ERROR);
                        return;
                    }

                    // Enter communication stage
                    // TODO: Add demands for the server to meet lol
                    W2K.getLogger().info("Received valid hello packet from server. Sending configuration packet");
                    final CompoundBinaryTag demands = CompoundBinaryTag.builder()
                            .putBoolean("command_spy", true)
                            .putBoolean("send_heartbeats", true)
                            .build();
                    sendPacket(new WServerboundConfigurePacket(nextTransactionId(), demands));
                    setStage(Stage.CONFIGURATION);
                    return;
                }
                else
                {
                    W2K.getLogger().warn("Server replied with an unrecognized packet during handshake. Entering error state");
                    setStage(Stage.ERROR);
                    return;
                }
            }
            case CONFIGURATION:
            {
                if (packet instanceof WCommonErrorPacket)
                {
                    W2K.getLogger().warn("Server replied with an error during the configuration stage, entering \"error\" state -> {}",
                            ((WCommonErrorPacket) packet).getError().toString());
                    setStage(Stage.ERROR);
                    return;
                }
                else if (packet instanceof WClientboundConfigureAcknowledgePacket)
                {
                    // TODO: add server demand processing
                    configureAcknowledgePacket = (WClientboundConfigureAcknowledgePacket) packet;
                    W2K.getLogger().warn("Server replied with a valid configuration acknowledgement packet. Handshake completed");
                    setStage(Stage.READY);
                    return;
                }
                else
                {
                    W2K.getLogger().info("Server replied with an unrecognized packet during handshake. Entering error state");
                    setStage(Stage.ERROR);
                    return;
                }
            }
            // Modules will only be able to receive packets of the "READY" or "ANY" type
            case READY:
            case ANY:
            {
                eventBus.post(packet);
            }
        }
    }

    public void sendPacket(WPacket packet)
    {
        if (packetTranslator == null)
        {
            return;
        }

        packetTranslator.sendPacket(packet);
    }

    public int nextTransactionId()
    {
        return transactionId++;
    }

    public void setStage(final Stage stage)
    {
        W2K.getEventBus().post(new StateChangeEvent(this.stage, stage));
        this.stage = stage;
    }

    @Subscribe
    public void onJoin(RegisterPluginMessageEvent event)
    {
        W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                .filter(handler -> stage == Stage.HELLO)
                .ifPresent(handler ->
                        sendPacket(new WServerboundHelloPacket(nextTransactionId(), WPacket.protocolVersion, W2K.getInstance().getVersionAbstractionLayer().getVersion())));
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent disconnect)
    {
        transactionId = -1;
        stage = Stage.HELLO;
        helloPacket = null;
        configureAcknowledgePacket = null;
    }
}
