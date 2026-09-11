package me.videogamesm12.w2k.drivers.v1_20_1.mixin.wrapper;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IEntityEntry;
import me.videogamesm12.w2k.kernel.data.IEntitySelector;
import me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
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
    protected abstract Predicate<Entity> getPositionPredicate(Vec3d pos);

    @Shadow
    @Final
    private TypeFilter<Entity, ?> entityFilter;
    @Shadow
    @Final
    private int limit;

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
                .filter(entity -> getPositionPredicate(positionOffset.apply(player.getPos())).test((Entity) entity))
                .filter(entity -> entityFilter.downcast((Entity) entity) != null)
                .sorted(Comparator.comparingDouble(entity -> pos.distanceTo(((Entity) entity).getPos())))
                .limit(limit)
                .toList());

        return list;
    }

    /*@Override
    public IEntityEntry w2k$getClientEntity() throws CommandSyntaxException
    {
        final List<IEntityEntry> entries = w2k$getClientEntities();

        if (entries.isEmpty())
        {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
        }
        else if (entries.size() > 1)
        {
            throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
        }

        return entries.get(0);
    }*/
}
