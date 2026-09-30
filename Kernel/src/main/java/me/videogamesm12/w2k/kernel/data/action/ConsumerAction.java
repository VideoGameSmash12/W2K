package me.videogamesm12.w2k.kernel.data.action;

import com.google.common.base.Preconditions;

import java.util.function.Consumer;

public class ConsumerAction<I> extends AbstractAction<Void>
{
    private final Consumer<I> consumer;
    private final Class<I> type;

    public ConsumerAction(final String id, final String label, final String description, final Consumer<I> consumer, final Class<I> type)
    {
        super(id, label, description);
        this.consumer = consumer;
        this.type = type;
    }

    @Override
    public Void performAction(Object... arguments)
    {
        Preconditions.checkArgument(arguments.length == 1, "Missing or incorrect number of arguments");
        Preconditions.checkArgument(type.isInstance(arguments[0]), "Mismatched argument type (expected " + type.getName() + ", got " + arguments[0].getClass().getName() + ")");

        consumer.accept(type.cast(arguments[0]));
        return null;
    }
}
