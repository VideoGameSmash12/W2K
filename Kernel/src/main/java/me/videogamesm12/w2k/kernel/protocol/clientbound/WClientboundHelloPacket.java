package me.videogamesm12.w2k.kernel.protocol.clientbound;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.protocol.Stage;
import me.videogamesm12.w2k.kernel.protocol.WPacket;

import java.util.List;

@Getter
@WPacket.PacketMeta(id = {"w2k", "clientbound/hello"},
        direction = WPacket.PacketMeta.Direction.CLIENT_BOUND,
        stage = Stage.HELLO)
public class WClientboundHelloPacket extends WPacket
{
    private final int protocolVersion;
    private final String serverBrand;
    private final String serverVersion;
    private final List<String> features;

    public WClientboundHelloPacket(final long transactionId, final int protocolVersion, final String serverBrand, final String serverVersion, final List<String> features)
    {
        super(transactionId);
        this.protocolVersion = protocolVersion;
        this.serverBrand = serverBrand;
        this.serverVersion = serverVersion;
        this.features = features;
    }
}
