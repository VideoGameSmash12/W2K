package me.videogamesm12.w2k.drivers.v1_17.required;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.driver.base.WCommandDriver;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.ArrayUtils;

@WDriverMetadata(identifier = "17_command_wrapper", minVersion = "1.17.1", maxVersion = "1.17.1", minProtocolVersion = 0, maxProtocolVersion = 0)
public class W117CommandDriver implements WCommandDriver
{
    @Override
    public void registerCommand(WCommand command)
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

        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal(command.getName()).executes(wrapped)
                .then(ClientCommandManager.argument("args", StringArgumentType.greedyString()).executes(wrapped)));
    }
}
