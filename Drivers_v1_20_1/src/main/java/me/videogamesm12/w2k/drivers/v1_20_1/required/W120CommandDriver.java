package me.videogamesm12.w2k.drivers.v1_20_1.required;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.driver.base.WCommandDriver;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

@WDriverMetadata(identifier = "120_command_wrapper")
public class W120CommandDriver implements WCommandDriver
{
    private final Map<Class<?>, ArgumentType<?>> argumentTypeMap = new HashMap<>();

    public W120CommandDriver()
    {
        argumentTypeMap.put(String.class, StringArgumentType.string());
        //argumentTypeMap.put(String.class.getName() + " (greedy)", StringArgumentType.greedyString());
        argumentTypeMap.put(Integer.class, IntegerArgumentType.integer());
        argumentTypeMap.put(UUID.class, (ArgumentType<UUID>) reader ->
        {
            try
            {
                return UUID.fromString(reader.readString());
            }
            catch (IllegalArgumentException ex)
            {
                return null;
            }
        });
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

            final Map<String, Class<?>> map = new HashMap<>();

            for (final Parameter parameter : method.getParameters())
            {
                W2K.getLogger().warn("Debug - Parameter {} - getName {} - getType {}", parameter, parameter.getName(), parameter.getType());
                if (!argumentTypeMap.containsKey(parameter.getType()))
                {
                    W2K.getLogger().error("Invalid parameter type - {}", parameter.getType().getName());
                    return;
                }

                tree.add(ClientCommandManager.argument(parameter.getName(), argumentTypeMap.get(parameter.getType())));
                map.put(parameter.getName(), parameter.getType());
            }

            // Add executable property to the last of the tree
            tree.set(tree.size() - 1, tree.get(tree.size() - 1).executes(ctx ->
            {
                try
                {
                    method.invoke(command, map.entrySet().stream().map(entry -> ctx.getArgument(entry.getKey(), entry.getValue())).toArray());
                }
                catch (Throwable ex)
                {
                    W2K.getLogger().error("FUCK", ex);
                }

                return 1;
            }));

            /*ArgumentBuilder<FabricClientCommandSource, ? extends ArgumentBuilder<FabricClientCommandSource, ?>> last = null;
            for (ArgumentBuilder<FabricClientCommandSource, ? extends ArgumentBuilder<FabricClientCommandSource, ?>> object : tree)
            {
                if (last == null)
                {
                    last = object;
                    continue;
                }


                W2K.getLogger().info("Debug - post-then arguments {}", object.getArguments());
            }*/

            List<CommandNode<FabricClientCommandSource>> builtTree = new ArrayList<>();
            /*for (int i = tree.size() - 1; i > 0; i--)
            {
                final ArgumentBuilder<FabricClientCommandSource, ? extends ArgumentBuilder<FabricClientCommandSource, ?>> builder = tree.get(i);
                if (builtTree.isEmpty())
                {
                    builtTree.add(builder.build());
                    continue;
                }


            }*/

            for (ArgumentBuilder<FabricClientCommandSource, ? extends ArgumentBuilder<FabricClientCommandSource, ?>> object : tree)
            {
                if (builtTree.isEmpty())
                {
                    W2K.getLogger().error("Debug - Built tree is empty, so just adding first entry");
                    builtTree.add(object.build());
                    continue;
                }

                CommandNode<FabricClientCommandSource> built = object.build();
                builtTree.get(builtTree.size() - 1).addChild(built);
                builtTree.add(built);

                /*
                W2K.getLogger().error("Debug - getArguments(): {}", builtTree.get(builtTree.size() - 1).getArguments());*/
            }

            int lol = 0;
            for (CommandNode<FabricClientCommandSource> node : builtTree)
            {
                W2K.getLogger().error("Debug - {} - getChildren(): {}", lol++, node.getChildren());
            }


            ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) ->
                    ((DispatcherHook<FabricClientCommandSource>) dispatcher).w2k$register(builtTree.get(0)));

            /*
            List<ArgumentBuilder<FabricClientCommandSource, ? extends ArgumentBuilder<FabricClientCommandSource, ?>>> builtTree = new ArrayList<>();

            for (ArgumentBuilder<FabricClientCommandSource, ? extends ArgumentBuilder<FabricClientCommandSource, ?>> object : tree)
            {
                if (builtTree.isEmpty())
                {
                    W2K.getLogger().error("Debug - Built tree is empty, so just adding first entry");
                    builtTree.add(object);
                    continue;
                }

                builtTree.add(builtTree.get(builtTree.size() - 1).then(object));
                W2K.getLogger().error("Debug - getArguments(): {}", builtTree.get(builtTree.size() - 1).getArguments());
            }

            ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) ->
                    dispatcher.register((LiteralArgumentBuilder<FabricClientCommandSource>) builtTree.get(builtTree.size() - 1)));
             */

            /*ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) ->
                    dispatcher.register((LiteralArgumentBuilder<FabricClientCommandSource>) tree.get(0)));*/
        });
    }

    public interface DispatcherHook<S>
    {
        void w2k$register(final CommandNode<S> node);
    }
}
