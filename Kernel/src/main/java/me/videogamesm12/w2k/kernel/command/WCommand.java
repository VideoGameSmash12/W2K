package me.videogamesm12.w2k.kernel.command;

import lombok.Getter;
import lombok.Setter;
import me.videogamesm12.w2k.kernel.W2K;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

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
    private final List<CommandPath<?, ?>> paths;

    protected WCommand()
    {
        if (!getClass().isAnnotationPresent(Parameters.class))
        {
            throw new IllegalArgumentException("Commands must have the Parameters class to be initialized this way");
        }

        final Parameters parameters = getClass().getAnnotation(Parameters.class);

        this.name       = parameters.name();
        this.usage      = parameters.usage();
        this.paths      = new ArrayList<>();
    }

    public void addPath(final CommandPath<?, ?> path)
    {
        paths.add(path);
    }

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

    public static void cancelAllScheduledOperations()
    {
        scheduler.cancel();
    }

    @Getter
    @Setter
    public static abstract class CommandPath<N, R>
    {
        private final WCommand command;
        private final Method method;
        private final N node;
        private String path = null;

        public CommandPath(final WCommand command, final Method method, final Function<String, R> resolverResolver)
        {
            this.command = command;
            this.method = method;
            this.node = buildNode(resolverResolver);
        }

        public abstract N buildNode(final Function<String, R> resolverResolver);

        @Override
        public String toString()
        {
            return path;
        }
    }
}
