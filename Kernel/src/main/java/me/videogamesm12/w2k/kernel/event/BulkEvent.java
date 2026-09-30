package me.videogamesm12.w2k.kernel.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * <h1>BulkEvent</h1>
 * <p>Event that is called when you want to handle multiple events of the same type at the same time.</p>
 * @param <T>   An implementation of {@link CustomEvent}
 */
@Getter
@RequiredArgsConstructor
public class BulkEvent<T extends CustomEvent> extends CustomEvent
{
    private final List<T> events;
    private final Class<T> eventClass;

    public BulkEvent(Class<T> eventClass, T... events)
    {
        this.eventClass = eventClass;
        this.events = Arrays.asList(events);
    }

    public boolean applicable(Class<? extends CustomEvent> eventClass)
    {
        return this.eventClass.equals(eventClass) || eventClass.isAssignableFrom(this.eventClass);
    }
}
