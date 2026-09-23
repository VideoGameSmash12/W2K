package me.videogamesm12.w2k.kernel.data.action;

import com.google.common.base.Preconditions;

import java.util.function.BiFunction;
import java.util.function.Function;

public class BiFunctionAction<I1, I2, O> extends AbstractAction<O>
{
    private final BiFunction<I1, I2, O> function;
    private final Class<I1> type1;
    private final Class<I2> type2;

    public BiFunctionAction(final String id, final String label, final String description, final BiFunction<I1, I2, O> function, final Class<I1> type1, final Class<I2> type2)
    {
        super(id, label, description);
        this.function = function;
        this.type1 = type1;
        this.type2 = type2;
    }

    @Override
    public O performAction(Object... arguments)
    {
        Preconditions.checkArgument(arguments.length == 2, "Missing or incorrect number of arguments");
        Preconditions.checkArgument(type1.isInstance(arguments[0]), "Mismatched first argument type (expected " + type1.getName() + ", got " + arguments[0].getClass().getName() + ")");
        Preconditions.checkArgument(type2.isInstance(arguments[1]), "Mismatched second argument type (expected " + type2.getName() + ", got " + arguments[1].getClass().getName() + ")");

        return function.apply(type1.cast(arguments[0]), type2.cast(arguments[1]));
    }
}
