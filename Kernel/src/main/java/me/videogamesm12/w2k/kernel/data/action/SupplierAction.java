package me.videogamesm12.w2k.kernel.data.action;

import com.google.common.base.Preconditions;

import java.util.function.Supplier;

public class SupplierAction<O> extends AbstractAction<O>
{
    private final Supplier<O> supplier;

    public SupplierAction(final String id, final String label, final String description, final Supplier<O> supplier)
    {
        super(id, label, description);
        this.supplier = supplier;
    }

    @Override
    public O performAction(Object... arguments)
    {
        Preconditions.checkArgument(arguments.length == 0, "Missing or incorrect number of arguments");

        return supplier.get();
    }
}
