package me.videogamesm12.w2k.drivers.v26_2.protocol.both;

import me.videogamesm12.w2k.kernel.protocol.common.WCommonErrorPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class WPacketCodecs
{
    public static final StreamCodec<RegistryFriendlyByteBuf, WCommonErrorPacket> FUCK = StreamCodec.of((buffer, instance) ->
    {
        buffer.writeLong(instance.getTransactionId());
        buffer.writeInt(instance.getError().ordinal());
        buffer.writeUtf(instance.getMessage());
        buffer.writeBoolean(instance.isTerminationWorthy());
    }, (b) -> new WCommonErrorPacket(b.readLong(), b.readInt(), b.readUtf(), b.readBoolean()));

}
