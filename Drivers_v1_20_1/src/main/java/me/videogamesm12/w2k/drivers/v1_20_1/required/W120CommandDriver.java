package me.videogamesm12.w2k.drivers.v1_20_1.required;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import lombok.Getter;
import me.videogamesm12.w2k.drivers.v1_20_1.command.ClientEntityArgumentType;
import me.videogamesm12.w2k.drivers.v1_20_1.command.ExperimentArgumentType;
import me.videogamesm12.w2k.drivers.v1_20_1.command.OnlinePlayersArgumentType;
import me.videogamesm12.w2k.drivers.v1_20_1.command.WModuleArgumentType;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.AbstractArgumentResolver;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.data.IEntitySelector;
import me.videogamesm12.w2k.kernel.driver.base.WCommandDriver;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.experiments.KernelExperiments;
import me.videogamesm12.w2k.kernel.module.WModule;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@WDriverMetadata(identifier = "120_command_wrapper")
public class W120CommandDriver implements WCommandDriver
{
    private final Map<String, BrigadierArgumentResolver<?, ?>> resolverMap = new HashMap<>();

    public W120CommandDriver()
    {
        register(Identifier.of("brigadier", "bool"), boolean.class, BoolArgumentType.bool());
        register(Identifier.of("brigadier", "float"), float.class, FloatArgumentType.floatArg());
        register(Identifier.of("brigadier", "double"), double.class, DoubleArgumentType.doubleArg());
        register(Identifier.of("brigadier", "integer"), int.class, IntegerArgumentType.integer());
        register(Identifier.of("brigadier", "long"), long.class, LongArgumentType.longArg());
        register(Identifier.of("brigadier", "string"), String.class, StringArgumentType.string());
        //--
        register(Identifier.of("w2k", "greedy_string"), String.class, StringArgumentType.greedyString(), true);
        register(Identifier.of("w2k", "word_string"), String.class, StringArgumentType.word(), true);
        //--
        register(Identifier.of("w2k", "online_players/name"), String.class, OnlinePlayersArgumentType.names(), true);
        register(Identifier.of("w2k", "online_players/uuid"), UUID.class, new UuidArgumentType()
        {
            @Override
            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
            {
                return CommandSource.suggestMatching(W2K.getInstance().getDriverManager().getVersionBridge().getPlayerList().stream().map(entry -> entry.w2k$profile().getId().toString()), builder);
            }
        }, true);
        register(Identifier.of("w2k", "online_players/both"), String.class, OnlinePlayersArgumentType.both(), true);
        //--
        register(Identifier.of("w2k", "module"), WModule.class, WModuleArgumentType.all(), true);
        //--
        register(Identifier.of("w2k", "experiment/all"), Experiment.class, ExperimentArgumentType.all(), true);
        register(Identifier.of("w2k", "experiment/unavailable_only"), Experiment.class, ExperimentArgumentType.unavailableOnly(), true);
        register(Identifier.of("w2k", "experiment/runtime_only"), Experiment.class, ExperimentArgumentType.runtimeOnly(), true);
        register(Identifier.of("w2k", "experiment/togglable"), Experiment.class, ExperimentArgumentType.togglable(), true);
        //--
        register(Identifier.of("w2k", "wrapped/entity"), IEntitySelector.class, new ClientEntityArgumentType(EntityArgumentType.entity()), true);
        register(Identifier.of("w2k", "wrapped/entities"), IEntitySelector.class, new ClientEntityArgumentType(EntityArgumentType.entities()), true);
        register(Identifier.of("w2k", "wrapped/entity/player"), IEntitySelector.class, new ClientEntityArgumentType(EntityArgumentType.player()), true);
        register(Identifier.of("w2k", "wrapped/entities/players"), IEntitySelector.class, new ClientEntityArgumentType(EntityArgumentType.players()), true);
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

    public <T, AT extends ArgumentType<T>> void register(final BrigadierArgumentResolver<T, AT> resolver)
    {
        resolverMap.put(resolver.getIdentifier(), resolver);
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

    @Getter
    public static class BrigadierArgumentResolver<T, AT extends ArgumentType<T>> extends AbstractArgumentResolver<T>
    {
        private final Identifier minecraftIdentifier;
        private final AT argumentType;

        public BrigadierArgumentResolver(final Identifier identifier,
                                         final Class<T> rawClass,
                                         final AT argumentType,
                                         final boolean registerIfUnique)
        {
            super(identifier.toString(), rawClass);
            this.minecraftIdentifier = identifier;
            this.argumentType = argumentType;

            if (registerIfUnique && !Registries.COMMAND_ARGUMENT_TYPE.containsId(identifier))
            {
                ArgumentTypeRegistry.registerArgumentType(identifier, argumentType.getClass(), ConstantArgumentSerializer.of(() -> argumentType));
            }
        }

        @Override
        public T resolveArgument(String string)
        {
            throw new UnsupportedOperationException("This is only available in pre-Brigadier command APIs");
        }
    }

    public static class FabricCommandPath extends WCommand.CommandPath<CommandNode<FabricClientCommandSource>, BrigadierArgumentResolver<?, ?>>
    {
        public FabricCommandPath(WCommand command, Method method, java.util.function.Function<String, BrigadierArgumentResolver<?, ?>> resolverResolver)
        {
            super(command, method, resolverResolver);
        }

        @Override
        public CommandNode<FabricClientCommandSource> buildNode(final Function<String, BrigadierArgumentResolver<?, ?>> resolverResolver)
        {
            final Parameter[] methodParameters = getMethod().getParameters();
            //--
            final ExecutionPath pathAnnotation = getMethod().getAnnotation(ExecutionPath.class);
            final String[] path = pathAnnotation.value();
            //--
            final List<ArgumentBuilder<FabricClientCommandSource, ?>> nodes = new ArrayList<>();
            final Map<String, BrigadierArgumentResolver<?, ?>> argumentsToResolvers = new HashMap<>();

            // Ensure that root commands don't have arguments
            if (path.length == 0)
            {
                Preconditions.checkArgument(methodParameters.length == 0, "Annotation implied root command (meaning no parameters), got methods with parameters instead");
            }

            // Add the root command
            nodes.add(ClientCommandManager.literal(getCommand().getName()));

            // Scan the path
            for (final String pathEntry : path)
            {
                // Handle variable arguments
                // Example input would be like <name|w2k:online_players/uuid>
                if (pathEntry.startsWith("<") && pathEntry.endsWith(">"))
                {
                    final String read = pathEntry.substring(1, pathEntry.length() - 1);
                    final String[] entryArgs = read.split("\\|");

                    // Validation checks to make sure we're not off the rails from the get-go
                    Preconditions.checkArgument(!read.isBlank(), "Argument cannot be blank or empty");
                    Preconditions.checkArgument(entryArgs.length >= 2, "Argument must have at least a name and resolver specified");

                    final String name = entryArgs[0]; // Always first
                    final String resolverName = entryArgs[1]; // Always second
                    //final String[] arguments = ArrayUtils.subarray(entryArgs, 2, entryArgs.length);

                    // Make sure we have a valid resolver
                    Preconditions.checkArgument(resolverResolver.apply(resolverName) != null, "'" + resolverName + "' is not a valid resolver");

                    // Get the resolver
                    final BrigadierArgumentResolver<?, ?> resolver = resolverResolver.apply(resolverName);

                    // Build the node
                    argumentsToResolvers.put(name, resolver);
                    nodes.add(ClientCommandManager.argument(name, resolver.getArgumentType()));
                }
                // Handle subcommands
                else
                {
                    nodes.add(ClientCommandManager.literal(pathEntry));
                }
            }

            // Add executable property to last in the tree
            nodes.get(nodes.size() - 1).executes(ctx ->
            {
                try
                {
                    getMethod().invoke(getCommand(), argumentsToResolvers.entrySet().stream().map(entry -> ctx.getArgument(entry.getKey(), entry.getValue().getRawClass())).toArray());
                }
                catch (Throwable ex)
                {
                    getCommand().msg(Component.translatable("w2k.command.command_error", Component.text(ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : ex.getClass().getName()))
                            .color(NamedTextColor.RED));
                    W2K.getLogger().error("An error occurred whilst processing command '{}'", ctx.getInput(), ex);
                }

                return 0;
            });

            // Avoid parameter count mismatch
            Preconditions.checkArgument(argumentsToResolvers.size() == methodParameters.length, "Non-matching method parameter count (expected " + argumentsToResolvers.size() + ", got " + methodParameters.length);

            // Avoid parameter mismatch
            int current = 0;
            for (Map.Entry<String, BrigadierArgumentResolver<?, ?>> resolverEntry : argumentsToResolvers.entrySet())
            {
                Preconditions.checkArgument(resolverEntry.getValue().getRawClass().equals(methodParameters[current].getType()),
                        String.format("Mismatched parameter for argument %1$s (expected %2$s, got %3$s)",
                                resolverEntry.getKey(),
                                resolverEntry.getValue().getRawClass().getName(),
                                methodParameters[current].getType().getName()));
                current++;
            }

            // Build what the path will look like
            setPath(String.join(" -> ", path));

            final List<CommandNode<FabricClientCommandSource>> builtTree = new ArrayList<>();
            for (ArgumentBuilder<FabricClientCommandSource, ? extends ArgumentBuilder<FabricClientCommandSource, ?>> object : nodes)
            {
                if (builtTree.isEmpty())
                {
                    builtTree.add(object.build());
                    continue;
                }

                CommandNode<FabricClientCommandSource> built = object.build();
                builtTree.get(builtTree.size() - 1).addChild(built);
                builtTree.add(built);
            }

            // Finish
            return builtTree.get(0);
        }
    }
}
