package me.videogamesm12.w2k.val.v26_1_x.command;

import com.mojang.brigadier.arguments.ArgumentType;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.abstraction.command.AbstractArgumentResolver;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

@Getter
public class BrigadierArgumentResolver<T, AT extends ArgumentType<T>> extends AbstractArgumentResolver<T>
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

        if (registerIfUnique && !BuiltInRegistries.COMMAND_ARGUMENT_TYPE.containsKey(identifier))
        {
            ArgumentTypeRegistry.registerArgumentType(identifier, argumentType.getClass(), SingletonArgumentInfo.contextFree(() -> argumentType));
        }
    }

    @Override
    public T resolveArgument(String string)
    {
        throw new UnsupportedOperationException("This is only available in pre-Brigadier command APIs");
    }
}
