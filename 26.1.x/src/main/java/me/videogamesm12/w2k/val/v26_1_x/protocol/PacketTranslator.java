package me.videogamesm12.w2k.val.v26_1_x.protocol;

import io.netty.buffer.Unpooled;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.network.AbstractPacketTranslator;
import me.videogamesm12.wcom.Stage;
import me.videogamesm12.wcom.WPacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundCommandSpyPacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundConfigureAcknowledgePacket;
import me.videogamesm12.wcom.protocol.clientbound.WClientboundHelloPacket;
import me.videogamesm12.wcom.protocol.common.WCommonErrorPacket;
import me.videogamesm12.wcom.protocol.serverbound.WServerboundCommandPacket;
import me.videogamesm12.wcom.protocol.serverbound.WServerboundConfigurePacket;
import me.videogamesm12.wcom.protocol.serverbound.WServerboundHelloPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PacketTranslator extends AbstractPacketTranslator
{
    private final Map<Class<?>, Function<?, ? extends WPacket>> readerMap = new HashMap<>();
    private final Map<Class<?>, BiConsumer<? extends WPacket, ?>> writerMap = new HashMap<>();

    private final Map<String, CustomPacketPayload.Type> typeMap = new HashMap<>();
    private final Map<String, StreamCodec> codecMap = new HashMap<>();

    public PacketTranslator()
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
    }

    @Override
    public <T extends WPacket> void sendPacket(T packet)
    {
        final String[] id = packet.getPacketMeta().id();
        final Identifier packetId = Identifier.fromNamespaceAndPath(id[0], id[1]);

        final StreamCodec<RegistryFriendlyByteBuf, WrappedWPacket<T>> codec = codecMap.get(packetId.toString());
        final BiConsumer<T, RegistryFriendlyByteBuf> writer = (BiConsumer<T, RegistryFriendlyByteBuf>) writerMap.get(packet.getClass());

        final WrappedWPacket<T> wrappedPacket = new WrappedWPacket<>(packetId.toString(), packet, writer);
        final RegistryFriendlyByteBuf byteBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), Objects.requireNonNull(Minecraft.getInstance().level).registryAccess());
        codec.encode(byteBuf, wrappedPacket);

        ClientPlayNetworking.send(wrappedPacket);
    }

    public <P extends WPacket> void register(Class<P> packetClass, Function<RegistryFriendlyByteBuf, P> reader, BiConsumer<P, RegistryFriendlyByteBuf> writer)
    {
        final WPacket.PacketMeta meta = packetClass.getAnnotation(WPacket.PacketMeta.class);
        final WPacket.PacketMeta.Direction direction = meta.direction();

        final String[] id = meta.id();
        final String identifier = id[0] + ":" + id[1];

        readerMap.put(packetClass, reader);
        writerMap.put(packetClass, writer);

        final CustomPacketPayload.Type<WrappedWPacket<P>> type = WrappedWPacket.createId(identifier);
        final StreamCodec<RegistryFriendlyByteBuf, WrappedWPacket<P>> codec = WrappedWPacket.createCodec(identifier, reader, writer);

        typeMap.put(identifier, type);
        codecMap.put(identifier, codec);

        if (direction == WPacket.PacketMeta.Direction.CLIENT_BOUND || direction == WPacket.PacketMeta.Direction.BOTH)
        {
            PayloadTypeRegistry.clientboundPlay().register(type, codec);
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
                    W2K.getLogger().error("Failed to receive packet", ex);
                }
            });
        }

        if (direction == WPacket.PacketMeta.Direction.SERVER_BOUND || direction == WPacket.PacketMeta.Direction.BOTH)
        {
            PayloadTypeRegistry.serverboundPlay().register(type, codec);
        }
    }
}
