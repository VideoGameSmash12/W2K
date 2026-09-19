package me.videogamesm12.w2k.val.v1_21_11.wrappers.command;

import me.videogamesm12.w2k.kernel.abstraction.command.EntitySelectorInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientPlayerEntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
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
    private @Nullable UUID uuid;

    @Shadow
    @Final
    private @Nullable String playerName;

    @Shadow
    @Final
    private boolean includesNonPlayers;

    @Shadow
    @Final
    private Function<Vec3d, Vec3d> positionOffset;

    @Shadow
    @Final
    private boolean senderOnly;

    @Shadow
    @Final
    private TypeFilter<Entity, ?> entityFilter;

    @Shadow
    @Final
    private int limit;

    @Shadow
    protected abstract Predicate<Entity> getPositionPredicate(Vec3d vec3d, Box box, FeatureSet featureSet);

    @Shadow
    protected abstract Box getOffsetBox(Vec3d vec3d);

    @Override
    public List<EntityInterface> w2k$getClientEntities()
    {
        final List<EntityInterface> list = new ArrayList<>();
        final Optional<ClientPlayerEntity> player = w2k$val().getLocalPlayer().map(wrapped -> (ClientPlayerEntity) wrapped);

        player.ifPresent(unwrapped ->
        {
            if (senderOnly)
            {
                list.add((ClientPlayerEntityInterface) unwrapped);
            }
            else
            {
                final Vec3d pos = unwrapped.getEntityPos();

                w2k$val().getLocalWorld().ifPresent(world -> list.addAll(world.w2k$getEntities().stream()
                        .filter(entity -> playerName == null || playerName.equalsIgnoreCase(entity.w2k$internalName()))
                        .filter(entity -> uuid == null || entity.w2k$uuid().equals(uuid))
                        .filter(entity -> entity.w2k$type().equalsIgnoreCase("minecraft:player") || includesNonPlayers)
                        .filter(entity ->
                        {
                            final Vec3d offset = positionOffset.apply(pos);
                            return getPositionPredicate(offset, getOffsetBox(offset), Objects.requireNonNull(MinecraftClient.getInstance().world).getEnabledFeatures()).test((Entity) entity);
                        })
                        .filter(entity -> entityFilter.downcast((Entity) entity) != null)
                        .sorted(Comparator.comparingDouble(entity -> pos.distanceTo(((Entity) entity).getEntityPos())))
                        .limit(limit)
                        .toList()));
            }
        });

        return list;
    }

}
