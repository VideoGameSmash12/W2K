package me.videogamesm12.w2k.drivers.v1_21_4.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.module.WModule;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class WModuleArgumentType implements ArgumentType<WModule>
{
    private static final Text UNKNOWN_MODULE = Text.translatable("w2k.command.error.unknown_module");

    public static WModuleArgumentType all()
    {
        return new WModuleArgumentType();
    }

    @Override
    public WModule parse(StringReader reader) throws CommandSyntaxException
    {
        return Optional.ofNullable(W2K.getInstance().getModuleManager().getModule(Identifier.fromCommandInput(reader).toString()))
                .map(module -> (WModule) module)
                .orElseThrow(() -> new CommandSyntaxException(new SimpleCommandExceptionType(UNKNOWN_MODULE), UNKNOWN_MODULE));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return CommandSource.suggestMatching(W2K.getInstance().getModuleManager().getIdRegistry().keySet(), builder);
    }
}
