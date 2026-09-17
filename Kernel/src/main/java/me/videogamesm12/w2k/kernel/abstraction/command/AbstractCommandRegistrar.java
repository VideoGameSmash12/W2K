package me.videogamesm12.w2k.kernel.abstraction.command;

import me.videogamesm12.w2k.kernel.command.WCommand;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCommandRegistrar<Resolver extends AbstractArgumentResolver<?>>
{
    protected final Map<String, Resolver> resolverMap = new HashMap<>();

    protected AbstractCommandRegistrar()
    {
        registerArgumentResolvers();
    }

    protected abstract void registerArgumentResolvers();

    public final void register(final Resolver resolver)
    {
        resolverMap.put(resolver.getIdentifier(), resolver);
    }

    public abstract void registerCommand(final WCommand command);
}
