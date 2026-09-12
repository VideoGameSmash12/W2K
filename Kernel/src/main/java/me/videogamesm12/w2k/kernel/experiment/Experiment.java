package me.videogamesm12.w2k.kernel.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class Experiment
{
    private final String identifier;
    private final String translatableName;
    private final String translatableDescription;
    private final boolean parameterOnly;
    private List<Condition> availability = Collections.emptyList();

    /**
     * Returns a translatable component for the Experiment's user-facing name.
     * @return  {@link Component}
     */
    public Component getName()
    {
        return Component.translatable(translatableName);
    }

    /**
     * Returns a translatable component for the Experiment's user-facing description.
     * @return  {@link Component}
     */
    public Component getDescription()
    {
        return translatableDescription != null ?
                Component.translatable(translatableDescription) :
                Component.empty();
    }

    /**
     * Returns a list of {@link Condition}s that weren't met.
     * @return  {@code List<Condition>}
     */
    public List<Condition> getFailedConditions()
    {
        return availability.stream().filter(condition -> !condition.conditionMet()).collect(Collectors.toList());
    }

    /**
     * Returns whether the conditions required for the experiment to be available have been met.
     * @return  True if there are no conditions present or if all of them have been met
     */
    public boolean isAvailable()
    {
        return availability.isEmpty() || availability.stream().allMatch(Condition::conditionMet);
    }
}
