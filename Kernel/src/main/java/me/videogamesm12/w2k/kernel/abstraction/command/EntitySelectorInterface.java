package me.videogamesm12.w2k.kernel.abstraction.command;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;

import java.util.List;

public interface EntitySelectorInterface extends ObjectInterface
{
    List<EntityInterface> w2k$getClientEntities();
}
