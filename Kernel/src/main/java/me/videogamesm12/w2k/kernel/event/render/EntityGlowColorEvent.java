package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EntityGlowColorEvent extends CustomEvent
{
    private final Object sync = new Object();
    private final List<Color> colors = new ArrayList<>();

    private EntityInterface entity;

    public EntityGlowColorEvent update(final EntityInterface entity)
    {
        synchronized (sync)
        {
            setCancelled(false);
            colors.clear();
            this.entity = entity;
            return this;
        }
    }

    public void addColor(final Color color)
    {
        colors.add(color);
    }
}
