package me.videogamesm12.w2k.val.v1_21_11.protocol;

import io.netty.buffer.Unpooled;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.network.AbstractPacketTranslator;
import me.videogamesm12.wcom.Stage;
import me.videogamesm12.wcom.WPacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundCommandSpyPacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundConfigureAcknowledgePacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundHeartbeatPacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundHelloPacket;
import me.videogamesm12.wcom.protocol.common.WCommonErrorPacket;
import me.videogamesm12.wcom.protocol.serverbound.WServerboundCommandPacket;
import me.videogamesm12.wcom.protocol.serverbound.WServerboundConfigurePacket;
import me.videogamesm12.wcom.protocol.serverbound.WServerboundHelloPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketTranslator extends AbstractPacketTranslator
{
    private final Map<Class<?>, Function<?, ? extends WPacket>> readerMap = new HashMap<>();
    private final Map<Class<?>, BiConsumer<? extends WPacket, ?>> writerMap = new HashMap<>();

    private final Map<String, CustomPayload.Id> typeMap = new HashMap<>();
    private final Map<String, PacketCodec> codecMap = new HashMap<>();

    public PacketTranslator()
    {
        // COMMON
        register(WCommonErrorPacket.class,
                byteBuf -> new WCommonErrorPacket(byteBuf.readLong(), byteBuf.readInt(), byteBuf.readString(), byteBuf.readBoolean()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeInt(packet.getError().ordinal());
                    buffer.writeString(packet.getMessage());
                    buffer.writeBoolean(packet.isTerminationWorthy());
                });
        // CLIENT-BOUND
        register(WClientboundHelloPacket.class,
                byteBuf -> new WClientboundHelloPacket(byteBuf.readLong(), byteBuf.readInt(), byteBuf.readString(), byteBuf.readString(), Arrays.stream(byteBuf.readString().split(",")).toList()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeInt(packet.getProtocolVersion());
                    buffer.writeString(packet.getServerBrand());
                    buffer.writeString(packet.getServerVersion());
                    buffer.writeString(String.join(",", packet.getFeatures()));
                });
        register(WClientboundConfigureAcknowledgePacket.class,
                byteBuf -> new WClientboundConfigureAcknowledgePacket(byteBuf.readLong(), byteBuf.readString()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeString(packet.formatDemands());
                });
        register(WClientboundCommandSpyPacket.class,
                byteBuf -> new WClientboundCommandSpyPacket(UUID.fromString(byteBuf.readString()), byteBuf.readString(), byteBuf.readString()),
                (packet, buffer) ->
                {
                    buffer.writeString(packet.getUuid().toString());
                    buffer.writeString(packet.getUsername());
                    buffer.writeString(packet.getCommand());
                });
        register(WClientboundHeartbeatPacket.class,
                byteBuf -> new WClientboundHeartbeatPacket(byteBuf.readLong(), byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTimestamp());
                    buffer.writeDouble(packet.getOneMinute());
                    buffer.writeDouble(packet.getFiveMinutes());
                    buffer.writeDouble(packet.getTenMinutes());
                });

        // SERVER-BOUND
        register(WServerboundHelloPacket.class,
                byteBuf -> new WServerboundHelloPacket(byteBuf.readLong(), byteBuf.readInt(), byteBuf.readString()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeInt(packet.getProtocolVersion());
                    buffer.writeString(packet.getMinecraftVersion());
                });
        register(WServerboundConfigurePacket.class,
                byteBuf -> new WServerboundConfigurePacket(byteBuf.readLong(), byteBuf.readString()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeString(packet.formatDemands());
                });
        register(WServerboundCommandPacket.class,
                byteBuf -> new WServerboundCommandPacket(byteBuf.readLong(), byteBuf.readString()),
                (packet, buffer) ->
                {
                    buffer.writeLong(packet.getTransactionId());
                    buffer.writeString(packet.getMessage());
                });
    }

    @Override
    public <T extends WPacket> void sendPacket(T packet)
    {
        final String[] id = packet.getPacketMeta().id();
        final net.minecraft.util.Identifier packetId = Identifier.of(id[0], id[1]);

        final PacketCodec<PacketByteBuf, WrappedWPacket<T>> codec = codecMap.get(packetId.toString());
        final BiConsumer<T, PacketByteBuf> writer = (BiConsumer<T, PacketByteBuf>) writerMap.get(packet.getClass());

        final WrappedWPacket<T> wrappedPacket = new WrappedWPacket<>(packetId.toString(), packet, writer);
        final PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
        codec.encode(byteBuf, wrappedPacket);

        ClientPlayNetworking.send(wrappedPacket);
    }

    public <P extends WPacket> void register(Class<P> packetClass, Function<PacketByteBuf, P> reader, BiConsumer<P, PacketByteBuf> writer)
    {
        final WPacket.PacketMeta meta = packetClass.getAnnotation(WPacket.PacketMeta.class);
        final WPacket.PacketMeta.Direction direction = meta.direction();

        final String[] id = meta.id();
        final String identifier = id[0] + ":" + id[1];

        readerMap.put(packetClass, reader);
        writerMap.put(packetClass, writer);

        final CustomPayload.Id<WrappedWPacket<P>> type = WrappedWPacket.createId(identifier);
        final PacketCodec<PacketByteBuf, WrappedWPacket<P>> codec = WrappedWPacket.createCodec(identifier, reader, writer);

        typeMap.put(identifier, type);
        codecMap.put(identifier, codec);

        if (direction == WPacket.PacketMeta.Direction.CLIENT_BOUND || direction == WPacket.PacketMeta.Direction.BOTH)
        {
            PayloadTypeRegistry.playS2C().register(type, codec);
            ClientPlayNetworking.registerGlobalReceiver(type, (wrappedPacket, ctx) ->
            {
                final P packet;

                try
                {
                    packet = wrappedPacket.toPacket();
                }
                catch (Exception ex)
                {
                    sendPacket(new WCommonErrorPacket(-1, WCommonErrorPacket.Error.INVALID_PARAMETER, ex.getMessage(), true));
                    W2K.getLogger().error("Server sent invalid packet to client", ex);
                    W2K.getInstance().getCommunicationManager().setStage(Stage.ERROR);
                    return;
                }

                try
                {
                    W2K.getInstance().getCommunicationManager().handlePacket(packet);
                }
                catch (Throwable ex)
                {
                    W2K.getLogger().error("Failed to handle packet", ex);
                    sendPacket(new WCommonErrorPacket(packet.getTransactionId(), WCommonErrorPacket.Error.UNKNOWN_ERROR, ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName(), true));
                }
            });
        }

        if (direction == WPacket.PacketMeta.Direction.SERVER_BOUND || direction == WPacket.PacketMeta.Direction.BOTH)
        {
            PayloadTypeRegistry.playC2S().register(type, codec);
        }
    }
}
