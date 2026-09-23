package me.videogamesm12.w2k.val.v26_2.wrappers.command;

import me.videogamesm12.w2k.kernel.abstraction.command.EntitySelectorInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientPlayerEntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public abstract class EntitySelectorWrapper implements EntitySelectorInterface
{
    @Shadow
    @Final
    private @Nullable UUID entityUUID;
    @Shadow
    @Final
    private @Nullable String playerName;
    @Shadow
    @Final
    private boolean includesEntities;

    @Shadow
    @Final
    private Function<Vec3, Vec3> position;
    @Shadow
    @Final
    private boolean currentEntity;

    @Shadow
    @Final
    private EntityTypeTest<Entity, ?> type;
    @Shadow
    @Final
    private int maxResults;

    @Shadow
    protected abstract Predicate<Entity> getPredicate(Vec3 par1, AABB par2, FeatureFlagSet par3);

    @Shadow
    @Nullable
    protected abstract AABB getAbsoluteAabb(Vec3 pos);

    @Override
    public List<EntityInterface> w2k$getClientEntities()
    {
        final List<EntityInterface> list = new ArrayList<>();
        final Optional<LocalPlayer> player = w2k$val().getLocalPlayer().map(wrapped -> (LocalPlayer) wrapped);

        player.ifPresent(unwrapped ->
        {
            if (currentEntity)
            {
                list.add((ClientPlayerEntityInterface) unwrapped);
            }
            else
            {
                final Vec3 pos = unwrapped.position();

                w2k$val().getLocalWorld().ifPresent(world -> list.addAll(world.w2k$getEntities().stream()
                        .filter(entity -> playerName == null || playerName.equalsIgnoreCase(entity.w2k$internalName()))
                        .filter(entity -> entityUUID == null || entity.w2k$uuid().equals(entityUUID))
                        .filter(entity -> entity.w2k$type().equalsIgnoreCase("minecraft:player") || includesEntities)
                        .filter(entity ->
                        {
                            final Vec3 offset = position.apply(pos);
                            return getPredicate(offset, getAbsoluteAabb(offset), Objects.requireNonNull(Minecraft.getInstance().level).enabledFeatures()).test((Entity) entity);
                        })
                        .filter(entity -> type.tryCast((Entity) entity) != null)
                        .sorted(Comparator.comparingDouble(entity -> pos.distanceTo(((Entity) entity).position())))
                        .limit(maxResults)
                        .toList()));
            }
        });

        return list;
    }
}
