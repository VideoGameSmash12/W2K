package me.videogamesm12.w2k.val.v26_2.command;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.WCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FabricCommandPath extends WCommand.CommandPath<CommandNode<FabricClientCommandSource>, BrigadierArgumentResolver<?, ?>>
{
    public FabricCommandPath(WCommand command, Method method, Function<String, BrigadierArgumentResolver<?, ?>> resolverResolver)
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
        nodes.add(ClientCommands.literal(getCommand().getName()).requires(source -> getCommand().available()));

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
                nodes.add(ClientCommands.argument(name, resolver.getArgumentType()));
            }
            // Handle subcommands
            else
            {
                nodes.add(ClientCommands.literal(pathEntry));
            }
        }

        // Add executable property to last in the tree
        nodes.getLast().executes(ctx ->
        {
            try
            {
                getMethod().invoke(getCommand(), argumentsToResolvers.entrySet().stream().map(entry -> ctx.getArgument(entry.getKey(), entry.getValue().getRawClass())).toArray());
            }
            catch (Throwable ex)
            {
                final Throwable whatToUse = ex.getCause() != null ? ex.getCause() : ex;

                getCommand().msg(Component.translatable("w2k.command.command_error", Component.text(whatToUse.getLocalizedMessage() != null ? whatToUse.getLocalizedMessage() : whatToUse.getClass().getName()))
                        .color(NamedTextColor.RED));
                W2K.getLogger().error("An error occurred whilst processing command '{}'", ctx.getInput(), whatToUse);
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
            builtTree.getLast().addChild(built);
            builtTree.add(built);
        }

        // Finish
        return builtTree.getFirst();
    }
}
