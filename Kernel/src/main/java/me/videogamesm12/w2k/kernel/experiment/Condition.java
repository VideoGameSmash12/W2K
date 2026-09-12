package me.videogamesm12.w2k.kernel.experiment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.loader.api.FabricLoader;

@RequiredArgsConstructor
@Getter
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

    public static Condition modLoaded(final String id)
    {
        return of("Requires mod '" + id + "'", FabricLoader.getInstance().isModLoaded(id));
    }
}
