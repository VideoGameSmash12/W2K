package me.videogamesm12.w2k.kernel.protocol.common;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.protocol.Stage;
import me.videogamesm12.w2k.kernel.protocol.WPacket;

@Getter
@WPacket.PacketMeta(id = {"w2k", "common/error"},
        direction = WPacket.PacketMeta.Direction.BOTH,
        stage = Stage.ANY)
public class WCommonErrorPacket extends WPacket
{
    private final Error error;
    private final String message;

    public WCommonErrorPacket(final long transactionId, final Error error, final String message)
    {
        super(transactionId);
        this.error = error;
        this.message = message;
    }

    public WCommonErrorPacket(final long transactionId, final int error, final String message)
    {
        this(transactionId, Error.fromCode(error), message);
    }

    @Override
    public String toString()
    {
        return error + ": " + message;
    }

    public enum Error
    {
        UNKNOWN_ERROR,
        UNSUPPORTED_W2K_VERSION,
        UNSUPPORTED_SERVER_VERSION,
        NO_PERMISSION,
        INVALID_PARAMETER,
        UNKNOWN_PLAYER,
        REQUEST_DISABLED,
        MESSAGE_TOO_LONG,
        INVALID_STAGE,
        ILLEGAL_REQUEST;

        private final String message;

        Error(final String message)
        {
            this.message = message;
        }

        Error()
        {
            this.message = null;
        }

        public static Error fromCode(int code)
        {
            final Error[] values = values();

            if (values.length < code || code < 0)
            {
                return UNKNOWN_ERROR;
            }

            return values[code];
        }
    }
}
