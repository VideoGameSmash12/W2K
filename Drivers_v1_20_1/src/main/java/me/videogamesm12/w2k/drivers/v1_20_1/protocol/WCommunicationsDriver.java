package me.videogamesm12.w2k.drivers.v1_20_1.protocol;

import io.netty.buffer.Unpooled;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.driver.base.WAmbassadorDriver;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.event.protocol.WPacketReceivedEvent;
import me.videogamesm12.w2k.kernel.protocol.Stage;
import me.videogamesm12.w2k.kernel.protocol.WPacket;
import me.videogamesm12.w2k.kernel.protocol.clientbound.WClientboundCommandSpyPacket;
import me.videogamesm12.w2k.kernel.protocol.clientbound.WClientboundConfigureAcknowledgePacket;
import me.videogamesm12.w2k.kernel.protocol.clientbound.WClientboundHelloPacket;
import me.videogamesm12.w2k.kernel.protocol.common.WCommonErrorPacket;
import me.videogamesm12.w2k.kernel.protocol.serverbound.WServerboundCommandPacket;
import me.videogamesm12.w2k.kernel.protocol.serverbound.WServerboundConfigurePacket;
import me.videogamesm12.w2k.kernel.protocol.serverbound.WServerboundHelloPacket;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;

@WDriverMetadata(identifier = "communications_driver")
public class WCommunicationsDriver implements WAmbassadorDriver
{
    private final Map<Class<? extends WPacket>, Function<PacketByteBuf, ? extends WPacket>> readerMap = new HashMap<>();
    private final Map<Class<? extends WPacket>, Function<? extends WPacket, PacketByteBuf>> writerMap = new HashMap<>();

    private int transactionId = -1;
    private Stage stage = Stage.HELLO;
    private WClientboundHelloPacket helloPacket = null;
    private WClientboundConfigureAcknowledgePacket configureAcknowledgePacket = null;

    public WCommunicationsDriver()
    {
        // COMMON
        register(WCommonErrorPacket.class,
                byteBuf -> new WCommonErrorPacket(byteBuf.readLong(), byteBuf.readInt(), byteBuf.readString(), byteBuf.readBoolean()),
                packet ->
                {
                    final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeInt(packet.getError().ordinal());
                    buffer.writeString(packet.getMessage());
                    buffer.writeBoolean(packet.isTerminationWorthy());
                    return buffer;
                });
        // CLIENT-BOUND
        register(WClientboundHelloPacket.class,
                byteBuf -> new WClientboundHelloPacket(byteBuf.readLong(), byteBuf.readInt(), byteBuf.readString(), byteBuf.readString(), Arrays.stream(byteBuf.readString().split(",")).toList()),
                packet ->
                {
                    final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeInt(packet.getProtocolVersion());
                    buffer.writeString(packet.getServerBrand());
                    buffer.writeString(packet.getServerVersion());
                    buffer.writeString(String.join(",", packet.getFeatures()));
                    return buffer;
                });
        register(WClientboundConfigureAcknowledgePacket.class,
                byteBuf -> new WClientboundConfigureAcknowledgePacket(byteBuf.readLong(), byteBuf.readString()),
                packet ->
                {
                    final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeString(packet.formatDemands());
                    return buffer;
                });
        register(WClientboundCommandSpyPacket.class,
                byteBuf -> new WClientboundCommandSpyPacket(UUID.fromString(byteBuf.readString()), byteBuf.readString(), byteBuf.readString()),
                packet ->
                {
                    final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                    buffer.writeString(packet.getUuid().toString());
                    buffer.writeString(packet.getUsername());
                    buffer.writeString(packet.getCommand());
                    return buffer;
                });
        // SERVER-BOUND
        register(WServerboundHelloPacket.class,
                byteBuf -> new WServerboundHelloPacket(byteBuf.readLong(), byteBuf.readInt(), byteBuf.readString()),
                packet ->
                {
                    final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeInt(packet.getProtocolVersion());
                    buffer.writeString(packet.getMinecraftVersion());
                    return buffer;
                });
        register(WServerboundConfigurePacket.class,
                byteBuf -> new WServerboundConfigurePacket(byteBuf.readLong(), byteBuf.readString()),
                packet -> {
                    final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeString(packet.formatDemands());
                    return buffer;
                });
        register(WServerboundCommandPacket.class,
                byteBuf -> new WServerboundCommandPacket(byteBuf.readLong(), byteBuf.readString()),
                packet ->
                {
                    final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeString(packet.getMessage());
                    return buffer;
                });

        C2SPlayChannelEvents.REGISTER.register((handler, sender, client, identifiers) ->
        {
            // insert check here for setting, that doesn't exist
            if (stage == Stage.HELLO)
            {
                sendPacket(new WServerboundHelloPacket(nextTransactionId(), WPacket.protocolVersion, "1.20.1"));
            }
        });

        // Reset everything upon disconnecting
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
        {
            transactionId = -1;
            stage = Stage.HELLO;
            helloPacket = null;
            configureAcknowledgePacket = null;
        });
    }

    @Override
    public <T extends WPacket> void receivePacket(String id, T packet)
    {
        W2K.getLogger().info("Receiving packet");

        // Ignore packets if we are in an "error" state
        if (stage == Stage.ERROR)
        {
            return;
        }

        final Stage packetStage = packet.getPacketMeta().stage();

        // Mismatched stages cause problems. Respond immediately with an error
        if (packetStage != Stage.ANY && stage != packet.getPacketMeta().stage())
        {
            W2K.getLogger().warn("Received mismatched packet stage from server (expected {}, got {})", stage, packetStage);
            sendPacket(new WCommonErrorPacket(packet.getTransactionId(), WCommonErrorPacket.Error.INVALID_STAGE, "Packet is for stage " + packetStage + ", but I am on stage " + stage, true));
            return;
        }

        if (packet instanceof WCommonErrorPacket error && error.isTerminationWorthy())
        {
            W2K.getLogger().warn("Received unrecoverable, non-negotiable error from server, entering \"error\" state -> {}: {}", error.getError(), error.getMessage());
            stage = Stage.ERROR;
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
                    stage = Stage.ERROR;
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
                        stage = Stage.ERROR;
                        return;
                    }

                    // Set the hello packet so we know it has already been sent
                    helloPacket = (WClientboundHelloPacket) packet;
                    if (helloPacket.getProtocolVersion() != WPacket.protocolVersion)
                    {
                        W2K.getLogger().warn("Server is using the wrong version of the protocol. Entering error state");
                        sendPacket(new WCommonErrorPacket(packet.getTransactionId(), WCommonErrorPacket.Error.UNSUPPORTED_W2K_VERSION, "I can only handle protocol version " + WPacket.protocolVersion, true));
                        stage = Stage.ERROR;
                        return;
                    }

                    // Enter communication stage
                    // TODO: Add demands for the server to meet lol
                    W2K.getLogger().info("Received valid hello packet from server. Sending configuration packet");
                    final CompoundBinaryTag demands = CompoundBinaryTag.builder().putBoolean("command_spy", true).build();
                    sendPacket(new WServerboundConfigurePacket(nextTransactionId(), demands));
                    stage = Stage.CONFIGURATION;
                    return;
                }
                else
                {
                    W2K.getLogger().warn("Server replied with an unrecognized packet during handshake. Entering error state");
                    stage = Stage.ERROR;
                    return;
                }
            }
            case CONFIGURATION:
            {
                if (packet instanceof WCommonErrorPacket)
                {
                    W2K.getLogger().warn("Server replied with an error during the configuration stage, entering \"error\" state -> {}",
                            ((WCommonErrorPacket) packet).getError().toString());
                    stage = Stage.ERROR;
                    return;
                }
                else if (packet instanceof WClientboundConfigureAcknowledgePacket)
                {
                    // TODO: add server demand processing
                    configureAcknowledgePacket = (WClientboundConfigureAcknowledgePacket) packet;
                    W2K.getLogger().warn("Server replied with a valid configuration acknowledgement packet. Handshake completed");
                    stage = Stage.READY;
                    return;
                }
                else
                {
                    W2K.getLogger().info("Server replied with an unrecognized packet during handshake. Entering error state");
                    stage = Stage.ERROR;
                    return;
                }
            }
            case READY:
            case ANY:
            {
                W2K.getLogger().info("Received packet, calling event");
                W2K.getEventBus().post(new WPacketReceivedEvent<>(id, packet));
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends WPacket> void sendPacket(T packet)
    {
        // Send nothing if we're in an error state
        if (stage == Stage.ERROR)
        {
            return;
        }

        final Function<T, PacketByteBuf> packetWriter = (Function<T, PacketByteBuf>) writerMap.get(packet.getClass());
        final String[] id = packet.getPacketMeta().id();
        final Identifier packetId = Identifier.of(id[0], id[1]);

        ClientPlayNetworking.send(Objects.requireNonNull(packetId),
                packetWriter.apply(packet));
    }

    @Override
    public Stage getStage()
    {
        return stage;
    }

    @Override
    public int nextTransactionId()
    {
        return transactionId++;
    }

    private <T extends WPacket> void register(final Class<T> packetClass,
                                              final Function<PacketByteBuf, T> reader,
                                              final Function<T, PacketByteBuf> writer)
    {
        final WPacket.PacketMeta meta = packetClass.getAnnotation(WPacket.PacketMeta.class);
        final WPacket.PacketMeta.Direction direction = meta.direction();

        final String[] id = meta.id();
        final Identifier identifier = Objects.requireNonNull(Identifier.of(id[0], id[1]));

        readerMap.put(packetClass, reader);
        writerMap.put(packetClass, writer);

        if (direction != WPacket.PacketMeta.Direction.SERVER_BOUND)
        {
            ClientPlayNetworking.registerGlobalReceiver(identifier, (client, handler, buf, responseSender) ->
            {
                if (stage == Stage.ERROR)
                {
                    return;
                }

                final T packet;

                try
                {
                    packet = reader.apply(buf);
                }
                catch (Exception ex)
                {
                    sendPacket(new WCommonErrorPacket(-1, WCommonErrorPacket.Error.INVALID_PARAMETER, ex.getMessage(), true));
                    W2K.getLogger().error("Server sent invalid packet to client", ex);

                    if (stage != Stage.READY && stage != Stage.ANY)
                    {
                        stage = Stage.ERROR;
                    }

                    return;
                }

                try
                {
                    receivePacket(identifier.toString(), packet);
                }
                catch (Throwable ex)
                {
                    W2K.getLogger().error("Failed to receive packet", ex);
                }
            });
        }
    }
}
