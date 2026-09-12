package me.videogamesm12.w2k.drivers.v26_2.mixin.wrapper;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IEntityEntry;
import me.videogamesm12.w2k.kernel.data.IEntitySelector;
import me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver;
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
import org.spongepowered.asm.mixin.Unique;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public abstract class EntitySelectorWrapper implements IEntitySelector
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

    @Unique
    private final WVersionBridgeDriver bridge = W2K.getInstance().getDriverManager().getVersionBridge();

    @Override
    public List<IEntityEntry> w2k$getClientEntities()
    {
        final List<IEntityEntry> list = new ArrayList<>();
        final LocalPlayer player = Minecraft.getInstance().player;

        if (player == null)
        {
            return List.of();
        }

        if (currentEntity)
        {
            list.add((IEntityEntry) player);
            return list;
        }

        final Vec3 pos = player.position();

        list.addAll(bridge.getEntities().stream()
                .filter(entity -> playerName == null || playerName.equalsIgnoreCase(entity.w2k$internalName()))
                .filter(entity -> entityUUID == null || entity.w2k$uuid().equals(entityUUID))
                .filter(entity -> entity.w2k$type().equalsIgnoreCase("minecraft:player") || includesEntities)
                .filter(entity -> {
                    final Vec3 offset = position.apply(pos);
                    return getPredicate(offset, getAbsoluteAabb(offset), Objects.requireNonNull(Minecraft.getInstance().level).enabledFeatures()).test((Entity) entity);
                })
                .filter(entity -> type.tryCast((Entity) entity) != null)
                .sorted(Comparator.comparingDouble(entity -> pos.distanceTo(((Entity) entity).position())))
                .limit(maxResults)
                .toList());

        return list;
    }
}
