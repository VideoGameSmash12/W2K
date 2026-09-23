package me.videogamesm12.w2k.val.v1_20_1.protocol;

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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;

public class PacketTranslator extends AbstractPacketTranslator
{
    private final Map<Class<? extends WPacket>, Function<PacketByteBuf, ? extends WPacket>> readerMap = new HashMap<>();
    private final Map<Class<? extends WPacket>, Function<? extends WPacket, PacketByteBuf>> writerMap = new HashMap<>();

    public PacketTranslator()
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
        register(WClientboundHeartbeatPacket.class,
                byteBuf -> new WClientboundHeartbeatPacket(byteBuf.readLong(), byteBuf.readDouble(), byteBuf.readDouble(), byteBuf.readDouble()),
                packet ->
                {
                    final PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
                    buffer.writeLong(packet.getTimestamp());
                    buffer.writeDouble(packet.getOneMinute());
                    buffer.writeDouble(packet.getFiveMinutes());
                    buffer.writeDouble(packet.getTenMinutes());
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
    }

    @Override
    public <T extends WPacket> void sendPacket(T packet)
    {
        final Function<T, PacketByteBuf> packetWriter = (Function<T, PacketByteBuf>) writerMap.get(packet.getClass());
        final String[] id = packet.getPacketMeta().id();
        final Identifier packetId = Identifier.of(id[0], id[1]);

        ClientPlayNetworking.send(Objects.requireNonNull(packetId),
                packetWriter.apply(packet));
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
                final T packet;

                try
                {
                    packet = reader.apply(buf);
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
    }
}
