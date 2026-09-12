package me.videogamesm12.w2k.drivers.v26_1.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.data.IEntitySelector;
import net.minecraft.commands.arguments.EntityArgument;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ClientEntityArgumentType implements ArgumentType<IEntitySelector>
{
    private final EntityArgument parent;

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
    public IEntitySelector parse(StringReader reader) throws CommandSyntaxException
    {
        return (IEntitySelector) parent.parse(reader);
    }
}
