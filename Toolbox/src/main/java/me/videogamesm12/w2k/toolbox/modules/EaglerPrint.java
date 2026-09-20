package me.videogamesm12.w2k.toolbox.modules;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.PlayerEntityInterface;
import me.videogamesm12.w2k.kernel.event.render.EntityGlowCheckEvent;
import me.videogamesm12.w2k.kernel.event.render.EntityGlowColorEvent;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.ColorSetting;

import java.awt.*;
import java.util.Objects;

public class EaglerPrint extends WModule
{
    public final ColorSetting highlightColor = register(new ColorSetting("highlight_color", "Highlight Color", new Color(0, 128, 255)));

    public EaglerPrint()
    {
        super("Eaglerprint",
                "Adds a \"marker\" to identify Eaglercraft users.");
    }

    @Subscribe
    public void onEntityGlowCheck(EntityGlowCheckEvent event)
    {
        if (!isEnabled() || !isEagler(event.getEntity()))
        {
            return;
        }

        event.setOutcome(true);
    }

    @Subscribe
    public void onEntityGlowColor(EntityGlowColorEvent event)
    {
        if (!isEnabled()
                || !isEagler(event.getEntity()))
        {
            return;
        }

        event.addColor(highlightColor.get());
        event.setCancelled(true);
    }

    public boolean isEagler(EntityInterface entity)
    {
        if (!(entity instanceof PlayerEntityInterface)
                || ((PlayerEntityInterface) entity).w2k$getGameProfile() == null
                || ((PlayerEntityInterface) entity).w2k$getGameProfile().w2k$properties() == null
                || ((PlayerEntityInterface) entity).w2k$getGameProfile().w2k$properties().values() == null)
        {
            return false;
        }

        return Objects.requireNonNull(((PlayerEntityInterface) entity).w2k$getGameProfile().w2k$properties().values()).stream()
                .anyMatch(property -> property.w2k$name().equalsIgnoreCase("isEaglerPlayer")
                        && property.w2k$value().equalsIgnoreCase("true"));
    }
}
