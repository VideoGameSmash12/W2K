package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;

import java.util.List;
import java.util.Map;

public interface ClientWorldInterface extends ObjectInterface
{
    List<EntityInterface> w2k$getEntities();

    List<BlockEntityInterface> w2k$getBlockEntities();

    Map<String, MapStateInterface> w2k$getMapStates();
}
