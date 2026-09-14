package me.videogamesm12.w2k.kernel.data.action;

import com.google.common.base.Preconditions;

import java.util.function.Function;

public class FunctionAction<I, O> extends AbstractAction<O>
{
    private final Function<I, O> function;
    private final Class<I> type;

    public FunctionAction(final String id, final String label, final String description, final Function<I, O> function, final Class<I> type)
    {
        super(id, label, description);
        this.function = function;
        this.type = type;
    }

    @Override
    public O performAction(Object... arguments)
    {
        Preconditions.checkArgument(arguments.length == 1, "Missing or incorrect number of arguments");
        Preconditions.checkArgument(type.isInstance(arguments[0]), "Mismatched argument type (expected " + type.getName() + ", got " + arguments[0].getClass().getName() + ")");

        return function.apply(type.cast(arguments[0]));
    }
}
