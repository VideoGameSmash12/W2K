package me.videogamesm12.w2k.kernel.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface WPacketProcessor<T>
{
    Map<Class<?>, Function<?, ? extends WPacket>> readerMap = new HashMap<>();
    Map<Class<?>, Function<? extends WPacket, ?>> writerMap = new HashMap<>();

    void registerCommonPackets();

    void registerClientboundPackets();

    void registerServerboundPackets();

    default <P extends WPacket> void register(Class<P> packetClass, Function<T, P> reader, Function<P, T> writer)
    {
        final WPacket.PacketMeta meta = packetClass.getAnnotation(WPacket.PacketMeta.class);
        final WPacket.PacketMeta.Direction direction = meta.direction();

        final String[] id = meta.id();
        final String identifier = id[0] + ":" + id[1];

        readerMap.put(packetClass, reader);
        writerMap.put(packetClass, writer);

        registerPlatform(identifier, direction, reader, writer);
    }

    <P extends WPacket> void registerPlatform(String identifier,
                                              WPacket.PacketMeta.Direction direction,
                                              Function<T, P> reader,
                                              Function<P, T> writer);
}
