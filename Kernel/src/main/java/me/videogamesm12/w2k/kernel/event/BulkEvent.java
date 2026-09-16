package me.videogamesm12.w2k.kernel.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

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
