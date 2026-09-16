package me.videogamesm12.w2k.val.v1_20_1.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.abstraction.command.EntitySelectorInterface;
import net.minecraft.command.argument.EntityArgumentType;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ClientEntityArgumentType implements ArgumentType<EntitySelectorInterface>
{
    private final EntityArgumentType parent;

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return parent.listSuggestions(context, builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return parent.getExamples();
    }

    @Override
    public EntitySelectorInterface parse(StringReader reader) throws CommandSyntaxException
    {
        return (EntitySelectorInterface) parent.parse(reader);
    }
}
