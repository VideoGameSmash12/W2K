package me.videogamesm12.w2k.kernel.experiment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Condition
{
    private final String label;
    private final boolean condition;

    public boolean conditionMet()
    {
        return condition;
    }

    public static Condition of(String label, boolean condition)
    {
        return new Condition(label, condition);
    }
}
