package me.videogamesm12.w2k.kernel.data.packet;

public interface WPacket
{
    int protocolVersion = 0;

    default String id()
    {
        return meta().value();
    }

    String toNbt();

    default int transactionId()
    {
        return -1;
    }

    default WPacketMeta meta()
    {
        if (getClass().isAnnotationPresent(WPacketMeta.class))
        {
            throw new IllegalStateException("This packet does not have a metadata annotation");
        }

        return getClass().getDeclaredAnnotation(WPacketMeta.class);
    }

    default boolean transactional()
    {
        return meta().transactional();
    }
}
