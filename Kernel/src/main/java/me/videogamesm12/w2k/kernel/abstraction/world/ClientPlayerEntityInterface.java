package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;

public interface ClientPlayerEntityInterface extends PlayerEntityInterface
{
    void w2k$displayMessage(final Component component);

    List<ItemStackInterface> w2k$getInventory();
}