package me.videogamesm12.w2k.kernel.data.packet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WPacketMeta
{
    String value();

    Direction direction();

    boolean transactional() default false;

    enum Direction
    {
        CLIENTBOUND,
        SERVERBOUND,
        BOTH;

        public void enforce()
        {
            if (this == CLIENTBOUND)
            {
                throw new IllegalArgumentException("This packet cannot be sent by the client");
            }
        }
    }
}
