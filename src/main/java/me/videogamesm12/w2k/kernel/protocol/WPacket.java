package me.videogamesm12.w2k.kernel.protocol;

import lombok.Getter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Getter
public abstract class WPacket
{
    public static final int protocolVersion = 0;

    private final long transactionId;
    private final PacketMeta packetMeta;

    public WPacket(long transactionId)
    {
        this.transactionId = transactionId;

        if (!getClass().isAnnotationPresent(PacketMeta.class))
        {
            throw new IllegalStateException("Missing PacketMeta annotation");
        }

        this.packetMeta = getClass().getDeclaredAnnotation(PacketMeta.class);
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface PacketMeta
    {
        String[] id();

        Direction direction();

        Stage stage() default Stage.READY;

        enum Direction
        {
            CLIENT_BOUND,
            SERVER_BOUND,
            BOTH
        }
    }
}
