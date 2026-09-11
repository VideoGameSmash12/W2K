package me.videogamesm12.w2k.drivers.v1_20_1.command;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.experiment.Condition;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ExperimentArgumentType implements ArgumentType<Experiment>
{
    private static final Text INVALID_EXPERIMENT = Text.translatable("w2k.command.experiments.invalid_experiment");
    private static final Text PARAMETER_ONLY = Text.translatable("w2k.command.experiments.parameter_only");

    private final Filter filter;

    public static ExperimentArgumentType all()
    {
        return new ExperimentArgumentType(Filter.ALL);
    }

    public static ExperimentArgumentType runtimeOnly()
    {
        return new ExperimentArgumentType(Filter.RUNTIME);
    }

    public static ExperimentArgumentType unavailableOnly()
    {
        return new ExperimentArgumentType(Filter.UNAVAILABLE);
    }

    public static ExperimentArgumentType togglable()
    {
        return new ExperimentArgumentType(Filter.TOGGLABLE);
    }

    private static CommandSyntaxException createException(final Message message)
    {
        return new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
    }

    @Override
    public Experiment parse(StringReader reader) throws CommandSyntaxException
    {
        final Optional<Experiment> experiment = Experiment.findExperiment(reader.readString());

        if (experiment.isEmpty())
        {
            throw createException(INVALID_EXPERIMENT);
        }

        if (filter.applicable.test(experiment.get()))
        {
            return experiment.get();
        }

        throw createException(filter.errorMessage.apply(experiment.get()));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return CommandSource.suggestMatching(Arrays.stream(Experiment.values())
                .filter(filter.applicable)
                .map(Enum::name).toList(), builder);
    }

    @RequiredArgsConstructor
    public enum Filter
    {
        ALL(experiment -> true, ignored -> Text.translatable("w2k.command.experiments.invalid_experiment")),
        RUNTIME(Experiment::isParameterOnly, ignored -> Text.translatable("w2k.command.experiments.not_parameter_only")),
        UNAVAILABLE(experiment -> !experiment.isAvailable(), ignored -> Text.translatable("w2k.command.experiments.available")),
        TOGGLABLE(experiment -> experiment.isAvailable() && !experiment.isParameterOnly(), experiment ->
        {
            final String conditions = String.join(", ", experiment.getFailedConditions().stream().map(Condition::getLabel).toList());

            // Combined issue
            if (!experiment.isAvailable() && experiment.isParameterOnly())
            {
                return Text.translatable("w2k.command.experiments.parameter_only_and_unavailable").append(conditions);
            }

            // The experiment is available, but it can't be toggled
            if (experiment.isParameterOnly())
            {
                return Text.translatable("w2k.command.experiments.parameter_only");
            }

            // The experiment is not available
            return Text.translatable("w2k.command.experiments.unavailable").append(conditions);
        });

        private final Predicate<Experiment> applicable;
        private final Function<Experiment, Text> errorMessage;
    }
}
