package me.videogamesm12.w2k.kernel.data.packet.both;

import me.videogamesm12.w2k.kernel.data.packet.WPacketMeta;

@WPacketMeta(value = "w2k:common/error", direction = WPacketMeta.Direction.BOTH)
public interface WCommonErrorPacket
{
    long transactionId();

    Error error();

    String message();

    enum Error
    {
        UNKNOWN_ERROR,
        UNSUPPORTED_W2K_VERSION,
        UNSUPPORTED_SERVER_VERSION,
        NO_PERMISSION,
        INVALID_PARAMETER,
        UNKNOWN_PLAYER,
        REQUEST_DISABLED,
        MESSAGE_TOO_LONG,
        NOT_READY;

        private final String message;

        Error(final String message)
        {
            this.message = message;
        }

        Error()
        {
            this.message = null;
        }
    }
}
