package me.videogamesm12.w2k.kernel.protocol.serverbound;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.protocol.Stage;
import me.videogamesm12.w2k.kernel.protocol.WPacket;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;

import java.io.IOException;

@Getter
@WPacket.PacketMeta(id = {"w2k", "serverbound/configure"},
        direction = WPacket.PacketMeta.Direction.SERVER_BOUND,
        stage = Stage.CONFIGURATION)
public class WServerboundConfigurePacket extends WPacket
{
    private final CompoundBinaryTag demands;

    public WServerboundConfigurePacket(final long transactionId, final String demands)
    {
        super(transactionId);

        CompoundBinaryTag deserializedDemands;
        try
        {
            deserializedDemands = TagStringIO.get().asCompound(demands);
        }
        catch (IOException ex)
        {
            // probably more possible, so we'll just make it blank
            deserializedDemands = CompoundBinaryTag.empty();
            W2K.getLogger().warn("Failed to deserialize demands", ex);
        }

        this.demands = deserializedDemands;
    }

    public WServerboundConfigurePacket(final long transactionId, final CompoundBinaryTag demands)
    {
        super(transactionId);
        this.demands = demands;
    }

    public String formatDemands()
    {
        try
        {
            return TagStringIO.get().asString(demands);
        }
        catch (IOException ex)
        {
            // probably impossible
            W2K.getLogger().warn("Failed to serialize demands", ex);
            return "{}";
        }
    }
}
