package me.videogamesm12.w2k.val.v1_21_11.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayerListEntryInterface;
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
                    .map(entry -> List.of(entry.w2k$profile().w2k$name(), entry.w2k$profile().w2k$uuid().toString()))
                    .flatMap(Collection::stream), builder);
        }

        return CommandSource.suggestMatching(getOnlinePlayers().stream().map(entry -> entry.w2k$profile().w2k$name()), builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }

    private List<PlayerListEntryInterface> getOnlinePlayers()
    {
        return W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                .map(PlayNetworkHandlerInterface::w2k$getOnlinePlayers)
                .orElseThrow(() -> new IllegalStateException("Not connected to a server"));
    }
}
