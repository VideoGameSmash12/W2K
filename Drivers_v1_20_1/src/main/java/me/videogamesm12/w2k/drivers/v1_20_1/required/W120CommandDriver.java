package me.videogamesm12.w2k.drivers.v1_20_1.required;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import lombok.Getter;
import me.videogamesm12.w2k.drivers.v1_20_1.command.ClientEntityArgumentType;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.data.IEntitySelector;
import me.videogamesm12.w2k.kernel.data.IPlayerEntry;
import me.videogamesm12.w2k.kernel.driver.base.WCommandDriver;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
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
import net.minecraft.text.Text;
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
    private final Map<String, ArgumentResolver<?, ?>> resolverMap = new HashMap<>();

    public W120CommandDriver()
    {
        register(new ArgumentResolver<>(Identifier.of("brigadier", "bool"), boolean.class, BoolArgumentType.bool()));
        register(new ArgumentResolver<>(Identifier.of("brigadier", "float"), float.class, FloatArgumentType.floatArg()));
        register(new ArgumentResolver<>(Identifier.of("brigadier", "double"), double.class, DoubleArgumentType.doubleArg()));
        register(new ArgumentResolver<>(Identifier.of("brigadier", "integer"), int.class, IntegerArgumentType.integer()));
        register(new ArgumentResolver<>(Identifier.of("brigadier", "long"), long.class, LongArgumentType.longArg()));
        register(new ArgumentResolver<>(Identifier.of("brigadier", "string"), String.class, StringArgumentType.string()));
        //--
        register(new ArgumentResolver<>(Identifier.of("w2k", "greedy_string"), String.class, StringArgumentType.greedyString(), true));
        register(new ArgumentResolver<>(Identifier.of("w2k", "word_string"), String.class, StringArgumentType.word(), true));
        register(new ArgumentResolver<>(Identifier.of("w2k", "online_players/name"), String.class, new ArgumentType<String>()
        {
            @Override
            public String parse(StringReader reader) throws CommandSyntaxException
            {
                return reader.readString();
            }

            @Override
            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
            {
                return CommandSource.suggestMatching(getOnlinePlayers().stream().map(entry -> entry.w2k$profile().getName()), builder);
            }

            private List<IPlayerEntry> getOnlinePlayers()
            {
                return W2K.getInstance().getDriverManager().getVersionBridge().getPlayerList();
            }
        }, true));
        register(new ArgumentResolver<>(Identifier.of("w2k", "online_players/uuid"), UUID.class, new UuidArgumentType()
        {
            @Override
            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
            {
                return CommandSource.suggestMatching(W2K.getInstance().getDriverManager().getVersionBridge().getPlayerList().stream().map(entry -> entry.w2k$profile().getId().toString()), builder);
            }
        }, true));
        register(new ArgumentResolver<>(Identifier.of("w2k", "online_players/both"), String.class, new ArgumentType<String>()
        {
            @Override
            public String parse(StringReader reader) throws CommandSyntaxException
            {
                return reader.readString();
            }

            @Override
            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
            {
                return CommandSource.suggestMatching(getOnlinePlayers().stream()
                        .map(entry -> List.of(entry.w2k$profile().getName(), entry.w2k$profile().getId().toString()))
                        .flatMap(Collection::stream), builder);
            }

            private List<IPlayerEntry> getOnlinePlayers()
            {
                return W2K.getInstance().getDriverManager().getVersionBridge().getPlayerList();
            }
        }, true));
        register(new ArgumentResolver<>(Identifier.of("w2k", "module"), WModule.class, new ArgumentType<WModule>()
        {
            @Override
            public WModule parse(StringReader reader) throws CommandSyntaxException
            {
                final String name = Identifier.fromCommandInput(reader).toString();
                final Message errorMessage = Text.literal("Invalid module: " + name);

                return Optional.ofNullable(W2K.getInstance().getModuleManager().getModule(name))
                        .map(module -> (WModule) module)
                        .orElseThrow(() -> new CommandSyntaxException(new SimpleCommandExceptionType(errorMessage), errorMessage));
            }

            @Override
            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
            {
                return CommandSource.suggestMatching(W2K.getInstance().getModuleManager().getIdRegistry().keySet(), builder);
            }
        }, true));
        register(new ArgumentResolver<>(Identifier.of("w2k", "experiment"), Experiment.class, new ArgumentType<Experiment>()
        {
            @Override
            public Experiment parse(StringReader reader) throws CommandSyntaxException
            {
                final Message errorMessage = Text.translatable("w2k.command.experiments.invalid_experiment");
                return Experiment.findExperiment(reader.readString())
                        .orElseThrow(() -> new CommandSyntaxException(new SimpleCommandExceptionType(errorMessage), errorMessage));
            }

            @Override
            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
            {
                return CommandSource.suggestMatching(Arrays.stream(Experiment.values())
                        .filter(experiment -> !experiment.isParameterOnly() && experiment.isAvailable())
                        .map(Enum::name).toList(), builder);
            }
        }, true));
        register(new ArgumentResolver<>(Identifier.of("w2k", "wrapped/entity"), IEntitySelector.class, new ClientEntityArgumentType(EntityArgumentType.entity()), true));
        register(new ArgumentResolver<>(Identifier.of("w2k", "wrapped/entities"), IEntitySelector.class, new ClientEntityArgumentType(EntityArgumentType.entities()), true));
        register(new ArgumentResolver<>(Identifier.of("w2k", "wrapped/entity/player"), IEntitySelector.class, new ClientEntityArgumentType(EntityArgumentType.player()), true));
        register(new ArgumentResolver<>(Identifier.of("w2k", "wrapped/entities/players"), IEntitySelector.class, new ClientEntityArgumentType(EntityArgumentType.players()), true));
    }

    @Override
    public void registerCommand(WCommand command)
    {
        final List<Method> methods = Arrays.stream(command.getClass().getMethods()).filter(method -> method.isAnnotationPresent(ExecutionPath.class)).toList();

        // Fallback onto the old dispatcher system if no execution path is specified
        if (methods.isEmpty() || !ExperimentManager.isExperimentEnabled(Experiment.KERNEL_COMMAND_SYSTEM_OVERHAUL))
        {
            final Command<FabricClientCommandSource> wrapped = context ->
            {
                final String[] input = context.getInput().split(" ");

                // If the input is somehow blank, this is a problem!
                if (input.length == 0)
                {
                    return 1;
                }

                try
                {
                    if (!command.executeCommand(input[0], ArrayUtils.remove(input, 0)))
                    {
                        command.msg(Component.translatable("w2k.command.command_usage",
                                Component.text(command.getUsage().replace("<command>", input[0]))));
                    }
                }
                catch (Throwable ex)
                {
                    command.msg(Component.translatable("w2k.command.command_error", Component.text(ex.getLocalizedMessage())));
                }

                return 0;
            };

            ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) ->
                    dispatcher.register(ClientCommandManager.literal(command.getName()).executes(wrapped)
                            .then(ClientCommandManager.argument("args", StringArgumentType.greedyString()).executes(wrapped))));
            return;
        }

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

    public <T, AT extends ArgumentType<T>> void register(final ArgumentResolver<T, AT> resolver)
    {
        resolverMap.put(resolver.getName().toString(), resolver);
    }

    public static class FabricCommandPath extends WCommand.CommandPath<CommandNode<FabricClientCommandSource>, ArgumentResolver<?, ?>>
    {
        public FabricCommandPath(WCommand command, Method method, java.util.function.Function<String, ArgumentResolver<?, ?>> resolverResolver)
        {
            super(command, method, resolverResolver);
        }

        @Override
        public CommandNode<FabricClientCommandSource> buildNode(final Function<String, ArgumentResolver<?, ?>> resolverResolver)
        {
            final Parameter[] methodParameters = getMethod().getParameters();
            //--
            final ExecutionPath pathAnnotation = getMethod().getAnnotation(ExecutionPath.class);
            final String[] path = pathAnnotation.value();

            //final List<CommandNode<FabricClientCommandSource>> nodeTree = new ArrayList<>();
            final List<ArgumentBuilder<FabricClientCommandSource, ?>> nodes = new ArrayList<>();
            final Map<String, ArgumentResolver<?, ?>> argumentsToResolvers = new HashMap<>();

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
                    final ArgumentResolver<?, ?> resolver = resolverResolver.apply(resolverName);

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
                    getCommand().msg(Component.translatable("w2k.command.command_error", Component.text(ex.getLocalizedMessage()))
                            .color(NamedTextColor.RED));
                    W2K.getLogger().error("An error occurred whilst processing command '{}'", ctx.getInput(), ex);
                }

                return 0;
            });

            // Avoid parameter count mismatch
            Preconditions.checkArgument(argumentsToResolvers.size() == methodParameters.length, "Non-matching method parameter count (expected " + argumentsToResolvers.size() + ", got " + methodParameters.length);

            // Avoid parameter mismatch
            int current = 0;
            for (Map.Entry<String, ArgumentResolver<?, ?>> resolverEntry : argumentsToResolvers.entrySet())
            {
                Preconditions.checkArgument(resolverEntry.getValue().rawClass.equals(methodParameters[current].getType()),
                        String.format("Mismatched parameter for argument %1$s (expected %2$s, got %3$s)",
                                resolverEntry.getKey(),
                                resolverEntry.getValue().rawClass.getName(),
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

    /*@Getter
    public static class CommandPath
    {
        private final WCommand command;
        private final Method method;
        private final CommandNode<FabricClientCommandSource> node;
        private String path = null;

        public CommandPath(final WCommand command, final Method method, final Function<String, ArgumentResolver<?, ?>> resolverResolver)
        {
            this.command = command;
            this.method = method;
            this.node = buildNode(resolverResolver);
        }

        public CommandNode<FabricClientCommandSource> buildNode(final Function<String, ArgumentResolver<?, ?>> resolverResolver)
        {
            final Parameter[] methodParameters = method.getParameters();
            //--
            final ExecutionPath pathAnnotation = method.getAnnotation(ExecutionPath.class);
            final String[] path = pathAnnotation.value();

            //final List<CommandNode<FabricClientCommandSource>> nodeTree = new ArrayList<>();
            final List<ArgumentBuilder<FabricClientCommandSource, ?>> nodes = new ArrayList<>();
            final Map<String, ArgumentResolver<?, ?>> argumentsToResolvers = new HashMap<>();

            // Ensure that root commands don't have arguments
            if (path.length == 0)
            {
                Preconditions.checkArgument(methodParameters.length == 0, "Annotation implied root command (meaning no parameters), got methods with parameters instead");
            }

            // Add the root command
            nodes.add(ClientCommandManager.literal(command.getName()));

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
                    final ArgumentResolver<?, ?> resolver = resolverResolver.apply(resolverName);

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
                    method.invoke(command, argumentsToResolvers.entrySet().stream().map(entry -> ctx.getArgument(entry.getKey(), entry.getValue().getRawClass())).toArray());
                }
                catch (Throwable ex)
                {
                    command.msg(Component.translatable("w2k.command.command_error", Component.text(ex.getLocalizedMessage()))
                            .color(NamedTextColor.RED));
                    W2K.getLogger().error("An error occurred whilst processing command '{}'", ctx.getInput(), ex);
                }

                return 0;
            });

            // Avoid parameter count mismatch
            Preconditions.checkArgument(argumentsToResolvers.size() == methodParameters.length, "Non-matching method parameter count (Expected " + argumentsToResolvers.size() + ", got " + methodParameters.length);

            // Build what the path will look like
            this.path = String.join(" -> ", path);

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

        @Override
        public String toString()
        {
            return path;
        }
    }*/

    @Getter
    public static class ArgumentResolver<T, AT extends ArgumentType<T>>
    {
        private final Identifier name;
        private final Class<T> rawClass;
        private final AT argumentType;

        public ArgumentResolver(final Identifier name, final Class<T> rawClass, final AT argumentType)
        {
            this(name, rawClass, argumentType, false);
        }

        public ArgumentResolver(final Identifier name, final Class<T> rawClass, final AT argumentType, final boolean registerIfUnique)
        {
            this.name = name;
            this.rawClass = rawClass;
            this.argumentType = argumentType;

            if (registerIfUnique && !Registries.COMMAND_ARGUMENT_TYPE.containsId(name))
            {
                ArgumentTypeRegistry.registerArgumentType(name, argumentType.getClass(), ConstantArgumentSerializer.of(() -> argumentType));
            }
        }

        public ArgumentResolver(final Identifier name, final AT argumentType)
        {
            this.name = name;
            this.argumentType = argumentType;
            try
            {
                this.rawClass = (Class<T>) argumentType.getClass().getMethod("parse", StringReader.class).getReturnType();
            }
            catch (NoSuchMethodException ex)
            {
                // should be impossible, but you never know
                throw new RuntimeException(ex);
            }
        }

        public T resolveArgument(final CommandContext<FabricClientCommandSource> ctx, final StringReader reader) throws CommandSyntaxException
        {
            return argumentType.parse(reader);
        }
    }
}
