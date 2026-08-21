package me.videogamesm12.w2k.supervisor.components.fantasia.unix;

import me.videogamesm12.w2k.supervisor.components.fantasia.ConnectionType;

public class UnixDomainConnectionType extends ConnectionType<UnixDomainConnectionListener>
{
    public UnixDomainConnectionType()
    {
        super("w2k:unix", UnixDomainConnectionListener.class);
    }
}