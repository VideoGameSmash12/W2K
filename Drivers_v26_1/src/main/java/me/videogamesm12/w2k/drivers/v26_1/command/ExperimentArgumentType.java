package me.videogamesm12.w2k.drivers.v26_1.command;

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
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ExperimentArgumentType implements ArgumentType<Experiment>
{
    private static final Component INVALID_EXPERIMENT = Component.translatable("w2k.command.experiments.invalid_experiment");

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
        final Optional<Experiment> experiment = ExperimentManager.getExperiment(Identifier.read(reader).toString());

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
        return SharedSuggestionProvider.suggest(ExperimentManager.getRegisteredExperiments().stream()
                .filter(filter.applicable)
                .map(Experiment::getIdentifier).toList(), builder);
    }

    @RequiredArgsConstructor
    public enum Filter
    {
        ALL(_ -> true, ignored -> Component.translatable("w2k.command.experiments.invalid_experiment")),
        RUNTIME(Experiment::isParameterOnly, ignored -> Component.translatable("w2k.command.experiments.not_parameter_only")),
        UNAVAILABLE(experiment -> !experiment.isAvailable(), ignored -> Component.translatable("w2k.command.experiments.available")),
        TOGGLABLE(experiment -> experiment.isAvailable() && !experiment.isParameterOnly(), experiment ->
        {
            final String conditions = String.join(", ", experiment.getFailedConditions().stream().map(Condition::getLabel).toList());

            // Combined issue
            if (!experiment.isAvailable() && experiment.isParameterOnly())
            {
                return Component.translatable("w2k.command.experiments.parameter_only_and_unavailable").append(conditions);
            }

            // The experiment is available, but it can't be toggled
            if (experiment.isParameterOnly())
            {
                return Component.translatable("w2k.command.experiments.parameter_only");
            }

            // The experiment is not available
            return Component.translatable("w2k.command.experiments.unavailable").append(conditions);
        });

        private final Predicate<Experiment> applicable;
        private final Function<Experiment, Component> errorMessage;
    }
}
