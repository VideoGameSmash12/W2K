package me.videogamesm12.w2k.val.v26_1_x.wrappers.world;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityWrapper implements EntityInterface
{
    @Shadow
    public abstract Component getDisplayName();

    @Shadow
    @Nullable
    public abstract Component getCustomName();

    @Shadow
    public abstract Component getName();

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract UUID getUUID();

    @Shadow
    public abstract void saveWithoutId(ValueOutput output);

    @Shadow
    public abstract RegistryAccess registryAccess();

    @Shadow
    public abstract String getScoreboardName();

    @Shadow
    public abstract void discard();

    @Shadow
    private Vec3 position;

    @Shadow
    private int id;

    @Shadow
    private BlockPos blockPosition;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Unique
    private JsonElement cachedName = null;
    @Unique
    private int nameHash = 0;

    @Override
    public String w2k$internalName()
    {
        return getScoreboardName();
    }

    @Override
    public JsonElement w2k$name()
    {
        final Component whatToUse = getDisplayName() != null ?
                getDisplayName() :
                getCustomName() != null ?
                        getCustomName() :
                        getName();

        if (cachedName == null || nameHash != whatToUse.hashCode())
        {
            cachedName = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, whatToUse).result().orElse(null);
            nameHash = whatToUse.hashCode();
        }

        return cachedName;
    }

    @Override
    public String w2k$type()
    {
        return EntityType.getKey(getType()).toString();
    }

    @Override
    public double w2k$x()
    {
        return position.x;
    }

    @Override
    public double w2k$y()
    {
        return position.y;
    }

    @Override
    public double w2k$z()
    {
        return position.z;
    }

    @Override
    public BlockPosInterface w2k$blockPos()
    {
        return (BlockPosInterface) blockPosition;
    }

    @Override
    public int w2k$id()
    {
        return id;
    }

    @Override
    public UUID w2k$uuid()
    {
        return getUUID();
    }

    @Override
    public String w2k$data()
    {
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER))
        {
            final TagValueOutput output = TagValueOutput.createWithContext(reporter,
                    registryAccess());

            output.putString("id", w2k$type());
            saveWithoutId(output);

            return output.buildResult().toString();
        }
        catch (Throwable ex)
        {
            W2K.getLogger().error("Failed to save data for entity {}", w2k$uuid().toString(), ex);
            return "";
        }
    }

    @Override
    public void w2k$kill()
    {
        discard();
        Objects.requireNonNull(Minecraft.getInstance().level).removeEntity(id, Entity.RemovalReason.KILLED);
    }
}
