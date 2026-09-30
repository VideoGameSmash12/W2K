package me.videogamesm12.w2k.kernel.data.action;

public class RunnableAction extends AbstractAction<Void>
{
    private final Runnable runnable;

    public RunnableAction(final String id, final String label, final String description, final Runnable runnable)
    {
        super(id, label, description);
        this.runnable = runnable;
    }

    @Override
    public Void performAction(final Object... arguments)
    {
        runnable.run();
        return null;
    }
}
