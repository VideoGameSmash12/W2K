package me.videogamesm12.w2k.drivers.v1_21_4.mixin.wrapper;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IEntityEntry;
import me.videogamesm12.w2k.kernel.data.IEntitySelector;
import me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver;
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
import org.spongepowered.asm.mixin.Unique;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public abstract class EntitySelectorWrapper implements IEntitySelector
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
    protected abstract Predicate<Entity> getPositionPredicate(Vec3d par1, Box par2, FeatureSet par3);

    @Shadow
    @Final
    private @Nullable Box box;

    @Shadow
    protected abstract Box getOffsetBox(Vec3d par1);

    @Unique
    private final WVersionBridgeDriver bridge = W2K.getInstance().getDriverManager().getVersionBridge();

    @Override
    public List<IEntityEntry> w2k$getClientEntities()
    {
        final List<IEntityEntry> list = new ArrayList<>();
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null)
        {
            return List.of();
        }

        if (senderOnly)
        {
            list.add((IEntityEntry) player);
            return list;
        }

        final Vec3d pos = player.getPos();

        list.addAll(bridge.getEntities().stream()
                .filter(entity -> playerName == null || playerName.equalsIgnoreCase(entity.w2k$internalName()))
                .filter(entity -> uuid == null || entity.w2k$uuid().equals(uuid))
                .filter(entity -> entity.w2k$type().equalsIgnoreCase("minecraft:player") || includesNonPlayers)
                .filter(entity -> {
                    final Vec3d offset = positionOffset.apply(player.getPos());
                    return getPositionPredicate(offset, getOffsetBox(offset), Objects.requireNonNull(MinecraftClient.getInstance().world).getEnabledFeatures()).test((Entity) entity);
                })
                .filter(entity -> entityFilter.downcast((Entity) entity) != null)
                .sorted(Comparator.comparingDouble(entity -> pos.distanceTo(((Entity) entity).getPos())))
                .limit(limit)
                .toList());

        return list;
    }
}
