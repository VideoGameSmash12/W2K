package me.videogamesm12.w2k.drivers.v1_20_1.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IPlayerEntry;
import net.minecraft.command.CommandSource;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class OnlinePlayersArgumentType implements ArgumentType<String>
{
    private static final List<String> EXAMPLES = List.of("videogamesm12", "f97c0d7b-6413-4558-a409-88f09a8f9adb");

    private final boolean includeUuids;

    public static OnlinePlayersArgumentType both()
    {
        return new OnlinePlayersArgumentType(true);
    }

    public static OnlinePlayersArgumentType names()
    {
        return new OnlinePlayersArgumentType(false);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException
    {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        if (includeUuids)
        {
            return CommandSource.suggestMatching(getOnlinePlayers().stream()
                    .map(entry -> List.of(entry.w2k$profile().getName(), entry.w2k$profile().getId().toString()))
                    .flatMap(Collection::stream), builder);
        }

        return CommandSource.suggestMatching(getOnlinePlayers().stream().map(entry -> entry.w2k$profile().getName()), builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }

    private List<IPlayerEntry> getOnlinePlayers()
    {
        return W2K.getInstance().getDriverManager().getVersionBridge().getPlayerList();
    }
}
