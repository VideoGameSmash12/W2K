package me.videogamesm12.w2k.drivers.v26_2.required;

import io.netty.buffer.Unpooled;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.driver.base.WAmbassadorDriver;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.event.protocol.WPacketReceivedEvent;
import me.videogamesm12.wcom.Stage;
import me.videogamesm12.wcom.WPacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundCommandSpyPacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundConfigureAcknowledgePacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundHelloPacket;
import me.videogamesm12.wcom.protocol.common.WCommonErrorPacket;
import me.videogamesm12.wcom.protocol.serverbound.WServerboundCommandPacket;
import me.videogamesm12.wcom.protocol.serverbound.WServerboundConfigurePacket;
import me.videogamesm12.wcom.protocol.serverbound.WServerboundHelloPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ServerboundPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@WDriverMetadata(identifier = "communications_driver")
public class WCommunicationsDriver implements WAmbassadorDriver
{
    private final Map<Class<?>, Function<?, ? extends WPacket>> readerMap = new HashMap<>();
    private final Map<Class<?>, BiConsumer<? extends WPacket, ?>> writerMap = new HashMap<>();

    private final Map<String, CustomPacketPayload.Type> typeMap = new HashMap<>();
    private final Map<String, StreamCodec> codecMap = new HashMap<>();

    private int transactionId = -1;
    private Stage stage = Stage.HELLO;
    private WClientboundHelloPacket helloPacket = null;
    private WClientboundConfigureAcknowledgePacket configureAcknowledgePacket = null;

    public WCommunicationsDriver()
    {
        // COMMON
        register(WCommonErrorPacket.class,
                byteBuf -> new WCommonErrorPacket(byteBuf.readLong(), byteBuf.readInt(), byteBuf.readUtf(), byteBuf.readBoolean()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeInt(packet.getError().ordinal());
                    buffer.writeUtf(packet.getMessage());
                    buffer.writeBoolean(packet.isTerminationWorthy());
                });
        // CLIENT-BOUND
        register(WClientboundHelloPacket.class,
                byteBuf -> new WClientboundHelloPacket(byteBuf.readLong(), byteBuf.readInt(), byteBuf.readUtf(), byteBuf.readUtf(), Arrays.stream(byteBuf.readUtf().split(",")).toList()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeInt(packet.getProtocolVersion());
                    buffer.writeUtf(packet.getServerBrand());
                    buffer.writeUtf(packet.getServerVersion());
                    buffer.writeUtf(String.join(",", packet.getFeatures()));
                });
        register(WClientboundConfigureAcknowledgePacket.class,
                byteBuf -> new WClientboundConfigureAcknowledgePacket(byteBuf.readLong(), byteBuf.readUtf()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeUtf(packet.formatDemands());
                });
        register(WClientboundCommandSpyPacket.class,
                byteBuf -> new WClientboundCommandSpyPacket(UUID.fromString(byteBuf.readUtf()), byteBuf.readUtf(), byteBuf.readUtf()),
                (packet, buffer) ->
                {
                    buffer.writeUtf(packet.getUuid().toString());
                    buffer.writeUtf(packet.getUsername());
                    buffer.writeUtf(packet.getCommand());
                });

        // SERVER-BOUND
        register(WServerboundHelloPacket.class,
                byteBuf -> new WServerboundHelloPacket(byteBuf.readLong(), byteBuf.readInt(), byteBuf.readUtf()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeInt(packet.getProtocolVersion());
                    buffer.writeUtf(packet.getMinecraftVersion());
                });
        register(WServerboundConfigurePacket.class,
                byteBuf -> new WServerboundConfigurePacket(byteBuf.readLong(), byteBuf.readUtf()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeUtf(packet.formatDemands());
                });
        register(WServerboundCommandPacket.class,
                byteBuf -> new WServerboundCommandPacket(byteBuf.readLong(), byteBuf.readUtf()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeUtf(packet.getMessage());
                });


        ServerboundPlayChannelEvents.REGISTER.register((handler, s, c, identifiers) ->
        {
            if (stage == Stage.HELLO && Minecraft.getInstance().getConnection() != null)
            {
                sendPacket(new WServerboundHelloPacket(nextTransactionId(), WPacket.protocolVersion, "26.2"));
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
    public <T extends WPacket> void sendPacket(T packet)
    {
        final String[] id = packet.getPacketMeta().id();
        final Identifier packetId = Identifier.fromNamespaceAndPath(id[0], id[1]);

        final StreamCodec<RegistryFriendlyByteBuf, WWrappedPacket<T>> codec = codecMap.get(packetId.toString());
        final BiConsumer<T, RegistryFriendlyByteBuf> writer = (BiConsumer<T, RegistryFriendlyByteBuf>) writerMap.get(packet.getClass());

        final WWrappedPacket<T> wrappedPacket = new WWrappedPacket<>(packetId.toString(), packet, writer);
        final RegistryFriendlyByteBuf byteBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), Objects.requireNonNull(Minecraft.getInstance().level).registryAccess());
        codec.encode(byteBuf, wrappedPacket);

        ClientPlayNetworking.send(wrappedPacket);
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

    public <P extends WPacket> void register(Class<P> packetClass, Function<RegistryFriendlyByteBuf, P> reader, BiConsumer<P, RegistryFriendlyByteBuf> writer)
    {
        final WPacket.PacketMeta meta = packetClass.getAnnotation(WPacket.PacketMeta.class);
        final WPacket.PacketMeta.Direction direction = meta.direction();

        final String[] id = meta.id();
        final String identifier = id[0] + ":" + id[1];

        readerMap.put(packetClass, reader);
        writerMap.put(packetClass, writer);

        final CustomPacketPayload.Type<WWrappedPacket<P>> type = WWrappedPacket.createId(identifier);
        final StreamCodec<RegistryFriendlyByteBuf, WWrappedPacket<P>> codec = WWrappedPacket.createCodec(identifier, reader, writer);

        typeMap.put(identifier, type);
        codecMap.put(identifier, codec);

        if (direction == WPacket.PacketMeta.Direction.CLIENT_BOUND || direction == WPacket.PacketMeta.Direction.BOTH)
        {
            PayloadTypeRegistry.clientboundPlay().register(type, codec);
            ClientPlayNetworking.registerGlobalReceiver(type, (wrappedPacket, ctx) ->
            {
                if (stage == Stage.ERROR)
                {
                    return;
                }

                final P packet;

                try
                {
                    packet = wrappedPacket.toPacket();
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
                    receivePacket(identifier, packet);
                }
                catch (Throwable ex)
                {
                    W2K.getLogger().error("Failed to receive packet", ex);
                }
            });
        }

        if (direction == WPacket.PacketMeta.Direction.SERVER_BOUND || direction == WPacket.PacketMeta.Direction.BOTH)
        {
            PayloadTypeRegistry.serverboundPlay().register(type, codec);
        }
    }


    public static class WWrappedPacket<T extends WPacket> implements CustomPacketPayload
    {
        public static <T extends WPacket> CustomPacketPayload.Type<WWrappedPacket<T>> createId(String identifier)
        {
            return new CustomPacketPayload.Type<>(Identifier.parse(identifier));
        }

        public static <T extends WPacket> StreamCodec<RegistryFriendlyByteBuf, WWrappedPacket<T>> createCodec(String identifier, Function<RegistryFriendlyByteBuf, T> reader, BiConsumer<T, RegistryFriendlyByteBuf> writer)
        {
            return StreamCodec.of((buffer, instance) ->
                    instance.fromPacket(buffer), buffer -> new WWrappedPacket<>(identifier, reader, writer, buffer));
        }

        private final String identifier;
        private final Function<RegistryFriendlyByteBuf, T> reader;
        private final BiConsumer<T, RegistryFriendlyByteBuf> writer;
        private final RegistryFriendlyByteBuf buffer;

        private T packet;

        public WWrappedPacket(final String identifier,
                              final Function<RegistryFriendlyByteBuf, T> reader,
                              final BiConsumer<T, RegistryFriendlyByteBuf> writer,
                              final RegistryFriendlyByteBuf buffer)
        {
            this.identifier = identifier;
            this.reader = reader;
            this.writer = writer;
            this.buffer = buffer;

            if (reader != null)
            {
                this.packet = reader.apply(buffer);
            }
        }

        public WWrappedPacket(final String identifier,
                              final T packet,
                              final BiConsumer<T, RegistryFriendlyByteBuf> writer)
        {
            this.identifier = identifier;
            this.packet = packet;
            this.reader = null;
            this.writer = writer;
            this.buffer = null;
        }

        public T toPacket()
        {
            if (packet == null && reader != null)
            {
                this.packet = reader.apply(buffer);
            }

            return this.packet;
        }

        public void fromPacket(RegistryFriendlyByteBuf buffer)
        {
            if (packet == null)
            {
                throw new IllegalStateException("Packet hasn't yet been created. What gives?");
            }

            writer.accept(packet, buffer);
        }

        @Override
        public @NonNull Type<? extends CustomPacketPayload> type()
        {
            return createId(identifier);
        }
    }
}
