package me.videogamesm12.w2k.kernel.abstraction.command;

import me.videogamesm12.w2k.kernel.command.WCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>AbstractCommandRegistrar</h1>
 * <p>A class for registering client commands and generating execution paths for said commands.</p>
 * @param <Resolver>    An implementation of {@link AbstractArgumentResolver}
 */
public abstract class AbstractCommandRegistrar<Resolver extends AbstractArgumentResolver<?>>
{
    protected final Map<String, Resolver> resolverMap = new HashMap<>();

    protected AbstractCommandRegistrar()
    {
        registerArgumentResolvers();
    }

    /**
     * Registers all the core argument resolvers. This is called immediately after an implementation of this class gets
     *  initialized.
     */
    protected abstract void registerArgumentResolvers();

    /**
     * Registers an argument resolver.
     * @param resolver  An implementation of {@link AbstractArgumentResolver}
     */
    public final void register(final Resolver resolver)
    {
        resolverMap.put(resolver.getIdentifier(), resolver);
    }

    /**
     * Generates command nodes for and registers the given client command.
     * @param command   {@link WCommand}
     */
    public abstract void registerCommand(final WCommand command);
}
