package me.videogamesm12.w2k.drivers.v1_20_1.required;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.Argument;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.data.IPlayerEntry;
import me.videogamesm12.w2k.kernel.driver.base.WCommandDriver;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.BooleanSetting;
import me.videogamesm12.w2k.kernel.module.setting.StringSetting;
import me.videogamesm12.w2k.kernel.module.setting.WModuleSetting;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.command.CommandSource;
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
import java.util.function.Supplier;

@WDriverMetadata(identifier = "120_command_wrapper")
public class W120CommandDriver implements WCommandDriver
{
    private final Map<String, ArgumentResolver<?, ?>> resolverMap = new HashMap<>();

    public W120CommandDriver()
    {
        register(new ArgumentResolver<>(Identifier.of("brigadier", "bool"), boolean.class, BoolArgumentType.bool()));
        register(new ArgumentResolver<>(Identifier.of("brigadier", "string"), String.class, StringArgumentType.string()));
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

        methods.forEach(method ->
        {
            final String path = method.getAnnotation(ExecutionPath.class).value();

            List<ArgumentBuilder<FabricClientCommandSource, ? extends ArgumentBuilder<FabricClientCommandSource, ?>>> tree = new ArrayList<>();

            // -- COMMAND NODE BUILDING --

            tree.add(ClientCommandManager.literal(command.getName()));

            // Skip ahead
            if (!path.trim().isBlank())
            {
                final String[] arguments = path.split(" ");

                for (String argument : arguments)
                {
                    tree.add(ClientCommandManager.literal(argument));
                }
            }

            final Map<String, ArgumentResolver<?, ?>> resolvers = new HashMap<>(); // Argument Name, ArgumentResolver

            for (final Parameter parameter : method.getParameters())
            {
                ArgumentResolver<?, ?> resolver = null;
                String name = null;

                // Derive resolver and name from Argument annotation
                if (parameter.isAnnotationPresent(Argument.class))
                {
                    final Argument argument = parameter.getAnnotation(Argument.class);

                    // Determine a resolver if one is manually specified
                    if (!argument.resolver().trim().isBlank() && resolverMap.containsKey(argument.resolver()))
                    {
                        resolver = resolverMap.get(argument.resolver().trim());
                    }

                    if (!argument.label().trim().isBlank())
                    {
                        name = argument.label().trim();
                    }
                }

                // Derive name from parameter name if missing or invalid Argument annotation (probably ugly, but whatever)
                if (name == null)
                {
                    name = parameter.getName();
                }

                // Derive resolver from raw class type
                if (resolver == null)
                {
                    resolver = resolverMap.values().stream()
                            .filter(containedResolver -> containedResolver.getRawClass().equals(parameter.getType()))
                            .findAny()
                            .orElse(null);
                }

                // Well, we tried lol
                if (resolver == null)
                {
                    W2K.getLogger().error("Invalid or unknown parameter type - {}", parameter.getType().getName());
                    return;
                }

                resolvers.put(name, resolver);
                tree.add(ClientCommandManager.argument(name, resolver.getArgumentType()));
            }

            // Add executable property to the last of the tree
            tree.set(tree.size() - 1, tree.get(tree.size() - 1).executes(ctx ->
            {
                try
                {
                    method.invoke(command, resolvers.entrySet().stream().map(entry -> ctx.getArgument(entry.getKey(), entry.getValue().getRawClass())).toArray());
                }
                catch (Throwable ex)
                {
                    command.msg(Component.translatable("w2k.command.command_error", Component.text(ex.getLocalizedMessage()))
                            .color(NamedTextColor.RED));
                    W2K.getLogger().error("An error occurred whilst processing command '{}'", ctx.getInput(), ex);
                }

                return 1;
            }));

            // -- REGISTRATION --
            final List<CommandNode<FabricClientCommandSource>> builtTree = new ArrayList<>();
            for (ArgumentBuilder<FabricClientCommandSource, ? extends ArgumentBuilder<FabricClientCommandSource, ?>> object : tree)
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

            ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) ->
                    ((DispatcherHook<FabricClientCommandSource>) dispatcher).w2k$register(builtTree.get(0)));
        });
    }

    public <T, AT extends ArgumentType<T>> void register(final ArgumentResolver<T, AT> resolver)
    {
        resolverMap.put(resolver.getName().toString(), resolver);
    }

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

        /*@SuppressWarnings("unchecked")
        public static <A extends WModuleSetting<?, ?>, AT extends ArgumentType<A>> ArgumentResolver<A, AT> forSettingType(Identifier name, final Class<A> rawClass, final Supplier<List<String>> suggestions)
        {
            return (ArgumentResolver<A, AT>) new ArgumentResolver<>(name, rawClass, new ArgumentType<>()
            {
                @Override
                public A parse(StringReader reader) throws CommandSyntaxException
                {
                    final Identifier identifier = Identifier.fromCommandInput(reader);
                    final String[] path = identifier.getPath().split("/");

                    final String moduleId = identifier.getNamespace() + ":" + path[0];
                    final String settingName = String.join("/", ArrayUtils.subarray(path, 1, path.length));

                    final Message invalidModuleError = Text.literal("Invalid module: " + moduleId);
                    final Message unknownSettingError = Text.literal("Unknown setting: " + settingName);

                    return (A) Optional.ofNullable(Optional.ofNullable(W2K.getInstance().getModuleManager().getModule(moduleId))
                                    .map(module -> (WModule) module)
                                    .orElseThrow(() -> new CommandSyntaxException(new SimpleCommandExceptionType(invalidModuleError), invalidModuleError))
                                    .getSettings().get(settingName))
                            .orElseThrow(() -> new CommandSyntaxException(new SimpleCommandExceptionType(unknownSettingError), unknownSettingError));
                }

                @Override
                public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
                {
                    if (suggestions != null)
                    {
                        return CommandSource.suggestMatching(suggestions.get(), builder);
                    }

                    return ArgumentType.super.listSuggestions(context, builder);
                }
            }, true);
        }*/
    }

    public interface DispatcherHook<S>
    {
        void w2k$register(final CommandNode<S> node);
    }
}
