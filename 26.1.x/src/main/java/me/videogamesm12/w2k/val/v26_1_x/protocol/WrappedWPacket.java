package me.videogamesm12.w2k.val.v26_1_x.protocol;

import me.videogamesm12.wcom.WPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class WrappedWPacket<T extends WPacket> implements CustomPacketPayload
{

    public static <T extends WPacket> Type<WrappedWPacket<T>> createId(String identifier)
    {
        return new Type<>(Identifier.parse(identifier));
    }

    public static <T extends WPacket> StreamCodec<RegistryFriendlyByteBuf, WrappedWPacket<T>> createCodec(String identifier, Function<RegistryFriendlyByteBuf, T> reader, BiConsumer<T, RegistryFriendlyByteBuf> writer)
    {
        return StreamCodec.of((buffer, instance) ->
                instance.fromPacket(buffer), buffer -> new WrappedWPacket<>(identifier, reader, writer, buffer));
    }

    private final String identifier;
    private final Function<RegistryFriendlyByteBuf, T> reader;
    private final BiConsumer<T, RegistryFriendlyByteBuf> writer;
    private final RegistryFriendlyByteBuf buffer;

    private T packet;

    public WrappedWPacket(final String identifier,
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

    public WrappedWPacket(final String identifier,
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
