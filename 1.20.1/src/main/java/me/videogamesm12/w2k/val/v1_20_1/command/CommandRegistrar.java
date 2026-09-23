package me.videogamesm12.w2k.val.v1_20_1.command;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.command.AbstractCommandRegistrar;
import me.videogamesm12.w2k.kernel.abstraction.command.EntitySelectorInterface;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.val.v1_20_1.command.arguments.ClientEntityArgumentType;
import me.videogamesm12.w2k.val.v1_20_1.command.arguments.ExperimentArgumentType;
import me.videogamesm12.w2k.val.v1_20_1.command.arguments.OnlinePlayersArgumentType;
import me.videogamesm12.w2k.val.v1_20_1.command.arguments.WModuleArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.util.Identifier;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class CommandRegistrar extends AbstractCommandRegistrar<BrigadierArgumentResolver<?, ?>>
{
    @Override
    protected void registerArgumentResolvers()
    {
        // -- BASE BRIGADIER ARGUMENTS -- //
        register(Identifier.of("brigadier", "bool"), boolean.class, BoolArgumentType.bool());
        register(Identifier.of("brigadier", "float"), float.class, FloatArgumentType.floatArg());
        register(Identifier.of("brigadier", "double"), double.class, DoubleArgumentType.doubleArg());
        register(Identifier.of("brigadier", "integer"), int.class, IntegerArgumentType.integer());
        register(Identifier.of("brigadier", "long"), long.class, LongArgumentType.longArg());
        register(Identifier.of("brigadier", "string"), String.class, StringArgumentType.string());

        // -- BRIGADIER ARGUMENTS THAT OTHERWISE COULD NOT FIT IN OUR SYSTEM -- //
        register(Identifier.of("w2k", "greedy_string"), String.class, StringArgumentType.greedyString(), true);
        register(Identifier.of("w2k", "word_string"), String.class, StringArgumentType.word(), true);

        // -- EXTENSIONS OF MINECRAFT'S ARGUMENTS -- //
        register(Identifier.of("w2k", "online_players/name"), String.class, OnlinePlayersArgumentType.names(), true);
        register(Identifier.of("w2k", "online_players/uuid"), UUID.class, new UuidArgumentType()
        {
            @Override
            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
            {
                return CommandSource.suggestMatching(W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                        .map(handler -> handler.w2k$getOnlinePlayers().stream()
                                .map(entry -> entry.w2k$uuid().toString())
                                .toList())
                        .orElseThrow(() -> new IllegalStateException("Not connected to a server")), builder);
            }
        }, true);
        register(Identifier.of("w2k", "online_players/both"), String.class, OnlinePlayersArgumentType.both(), true);
        register(Identifier.of("w2k", "wrapped/entity"), EntitySelectorInterface.class, new ClientEntityArgumentType(EntityArgumentType.entity()), true);
        register(Identifier.of("w2k", "wrapped/entities"), EntitySelectorInterface.class, new ClientEntityArgumentType(EntityArgumentType.entities()), true);
        register(Identifier.of("w2k", "wrapped/entity/player"), EntitySelectorInterface.class, new ClientEntityArgumentType(EntityArgumentType.player()), true);
        register(Identifier.of("w2k", "wrapped/entities/players"), EntitySelectorInterface.class, new ClientEntityArgumentType(EntityArgumentType.players()), true);

        // -- CUSTOM W2K ARGUMENTS -- //
        register(Identifier.of("w2k", "module"), WModule.class, WModuleArgumentType.all(), true);
        register(Identifier.of("w2k", "experiment/all"), Experiment.class, ExperimentArgumentType.all(), true);
        register(Identifier.of("w2k", "experiment/unavailable_only"), Experiment.class, ExperimentArgumentType.unavailableOnly(), true);
        register(Identifier.of("w2k", "experiment/runtime_only"), Experiment.class, ExperimentArgumentType.runtimeOnly(), true);
        register(Identifier.of("w2k", "experiment/togglable"), Experiment.class, ExperimentArgumentType.togglable(), true);
    }

    @Override
    public void registerCommand(WCommand command)
    {
        final List<Method> methods = Arrays.stream(command.getClass().getMethods()).filter(method -> method.isAnnotationPresent(ExecutionPath.class)).toList();

        W2K.getLogger().debug("Scanning command class for executable paths");
        methods.forEach(method ->
        {
            if (method.isAnnotationPresent(ExecutionPath.class))
            {
                W2K.getLogger().debug("Building execution path for method {}", method);
                try
                {
                    final FabricCommandPath path = new FabricCommandPath(command, method, resolverMap::get);
                    W2K.getLogger().debug("Execution path for method {} completed: {}", method, path.toString());
                    ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) ->
                            dispatcher.getRoot().addChild(path.getNode()));
                    command.addPath(path);
                }
                catch (Throwable ex)
                {
                    W2K.getLogger().error("Failed to build and register execution path for method {}", method, ex);
                }
            }
        });
    }

    public <T, AT extends ArgumentType<T>> void register(final Identifier identifier,
                                                         final Class<T> rawClass,
                                                         final AT argumentType,
                                                         final boolean registerIfUnique)
    {
        register(new BrigadierArgumentResolver<>(identifier, rawClass, argumentType, registerIfUnique));
    }

    public <T, AT extends ArgumentType<T>> void register(final Identifier identifier,
                                                         final Class<T> rawClass,
                                                         final AT argumentType)
    {
        register(new BrigadierArgumentResolver<>(identifier, rawClass, argumentType, false));
    }

}
