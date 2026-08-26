package me.videogamesm12.w2k.kernel.protocol.serverbound;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.protocol.Stage;
import me.videogamesm12.w2k.kernel.protocol.WPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@WPacket.PacketMeta(id = {"w2k", "serverbound/configure"},
        direction = WPacket.PacketMeta.Direction.SERVER_BOUND,
        stage = Stage.HELLO)
public class WServerboundConfigurePacket extends WPacket
{
    private final Map<String, String> demands = new HashMap<>();

    public WServerboundConfigurePacket(final long transactionId, final String demands)
    {
        super(transactionId);

        for (String demandSet : demands.split(","))
        {
            final String[] strings = demandSet.split("=");
            if (strings.length != 2)
            {
                throw new IllegalArgumentException("More than 1 = in the set");
            }

            this.demands.put(strings[0], strings[1]);
        }
    }

    public String formatDemands()
    {
        return demands.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(","));
    }
}
