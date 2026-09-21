package me.videogamesm12.w2k.val.v26_1_x.command;

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
import me.videogamesm12.w2k.val.v26_1_x.command.arguments.ClientEntityArgument;
import me.videogamesm12.w2k.val.v26_1_x.command.arguments.ExperimentArgument;
import me.videogamesm12.w2k.val.v26_1_x.command.arguments.OnlinePlayersArgument;
import me.videogamesm12.w2k.val.v26_1_x.command.arguments.WModuleArgument;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommandRegistrar extends AbstractCommandRegistrar<BrigadierArgumentResolver<?, ?>>
{
    @Override
    protected void registerArgumentResolvers()
    {
        // -- BASE BRIGADIER ARGUMENTS -- //
        register(Identifier.fromNamespaceAndPath("brigadier", "bool"), boolean.class, BoolArgumentType.bool());
        register(Identifier.fromNamespaceAndPath("brigadier", "float"), float.class, FloatArgumentType.floatArg());
        register(Identifier.fromNamespaceAndPath("brigadier", "double"), double.class, DoubleArgumentType.doubleArg());
        register(Identifier.fromNamespaceAndPath("brigadier", "integer"), int.class, IntegerArgumentType.integer());
        register(Identifier.fromNamespaceAndPath("brigadier", "long"), long.class, LongArgumentType.longArg());
        register(Identifier.fromNamespaceAndPath("brigadier", "string"), String.class, StringArgumentType.string());

        // -- BRIGADIER ARGUMENTS THAT OTHERWISE COULD NOT FIT IN OUR SYSTEM -- //
        register(Identifier.fromNamespaceAndPath("w2k", "greedy_string"), String.class, StringArgumentType.greedyString(), true);
        register(Identifier.fromNamespaceAndPath("w2k", "word_string"), String.class, StringArgumentType.word(), true);

        // -- EXTENSIONS OF MINECRAFT'S ARGUMENTS -- //
        register(Identifier.fromNamespaceAndPath("w2k", "online_players/name"), String.class, OnlinePlayersArgument.names(), true);
        register(Identifier.fromNamespaceAndPath("w2k", "online_players/uuid"), UUID.class, new UuidArgument()
        {
            @Override
            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
            {
                return SharedSuggestionProvider.suggest(W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                        .map(handler -> handler.w2k$getOnlinePlayers().stream()
                                .map(entry -> entry.w2k$uuid().toString())
                                .toList())
                        .orElseThrow(() -> new IllegalStateException("Not connected to a server")), builder);
            }
        }, true);
        register(Identifier.fromNamespaceAndPath("w2k", "online_players/both"), String.class, OnlinePlayersArgument.both(), true);
        register(Identifier.fromNamespaceAndPath("w2k", "wrapped/entity"), EntitySelectorInterface.class, new ClientEntityArgument(EntityArgument.entity()), true);
        register(Identifier.fromNamespaceAndPath("w2k", "wrapped/entities"), EntitySelectorInterface.class, new ClientEntityArgument(EntityArgument.entities()), true);
        register(Identifier.fromNamespaceAndPath("w2k", "wrapped/entity/player"), EntitySelectorInterface.class, new ClientEntityArgument(EntityArgument.player()), true);
        register(Identifier.fromNamespaceAndPath("w2k", "wrapped/entities/players"), EntitySelectorInterface.class, new ClientEntityArgument(EntityArgument.players()), true);

        // -- CUSTOM W2K ARGUMENTS -- //
        register(Identifier.fromNamespaceAndPath("w2k", "module"), WModule.class, WModuleArgument.all(), true);
        register(Identifier.fromNamespaceAndPath("w2k", "experiment/all"), Experiment.class, ExperimentArgument.all(), true);
        register(Identifier.fromNamespaceAndPath("w2k", "experiment/unavailable_only"), Experiment.class, ExperimentArgument.unavailableOnly(), true);
        register(Identifier.fromNamespaceAndPath("w2k", "experiment/runtime_only"), Experiment.class, ExperimentArgument.runtimeOnly(), true);
        register(Identifier.fromNamespaceAndPath("w2k", "experiment/togglable"), Experiment.class, ExperimentArgument.togglable(), true);
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
