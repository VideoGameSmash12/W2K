package me.videogamesm12.w2k.kernel.command;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * <h1>WCommand</h1>
 * <p>A version-agnostic client command class.</p>
 */
@Getter
public abstract class WCommand
{
    private static final Timer scheduler = new Timer();

    private final String name;
    private final String usage;

    protected WCommand()
    {
        if (!getClass().isAnnotationPresent(Parameters.class))
        {
            throw new IllegalArgumentException("Commands must have the Parameters class to be initialized this way");
        }

        final Parameters parameters = getClass().getAnnotation(Parameters.class);

        this.name       = parameters.name();
        this.usage      = parameters.usage();
    }

    public abstract boolean executeCommand(String commandLabel, String[] args);

    public final void msg(@NotNull Component component)
    {
        Objects.requireNonNull(component);
        W2K.getInstance().getDriverManager().getVersionBridge().displayMessage(component);
    }

    public final void schedule(@NotNull Runnable task, int delay)
    {
        Objects.requireNonNull(task);
        scheduler.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                task.run();
            }
        }, delay);
    }
}
