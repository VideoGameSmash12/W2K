package me.videogamesm12.w2k.val.v1_21_11.protocol;

import me.videogamesm12.wcom.WPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class WrappedWPacket<T extends WPacket> implements CustomPayload
{
    public static <T extends WPacket> CustomPayload.Id<WrappedWPacket<T>> createId(String identifier)
    {
        return new CustomPayload.Id<>(Identifier.of(identifier));
    }

    public static <T extends WPacket> PacketCodec<PacketByteBuf, WrappedWPacket<T>> createCodec(String identifier, Function<PacketByteBuf, T> reader, BiConsumer<T, PacketByteBuf> writer)
    {
        return PacketCodec.of(WrappedWPacket::fromPacket, buffer -> new WrappedWPacket<>(identifier, reader, writer, buffer));
    }

    private final String identifier;
    private final Function<PacketByteBuf, T> reader;
    private final BiConsumer<T, PacketByteBuf> writer;
    private final PacketByteBuf buffer;

    private T packet;

    public WrappedWPacket(final String identifier,
                          final Function<PacketByteBuf, T> reader,
                          final BiConsumer<T, PacketByteBuf> writer,
                          final PacketByteBuf buffer)
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

    public WrappedWPacket(final String identifier,
                          final T packet,
                          final BiConsumer<T, PacketByteBuf> writer)
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

    public void fromPacket(PacketByteBuf buffer)
    {
        if (packet == null)
        {
            throw new IllegalStateException("Packet hasn't yet been created. What gives?");
        }

        writer.accept(packet, buffer);
    }

    @Override
    public @NonNull Id<? extends CustomPayload> getId()
    {
        return createId(identifier);
    }
}
