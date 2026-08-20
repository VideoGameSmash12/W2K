package me.videogamesm12.w2k.drivers.v26_2.mixin.wrapper;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IEntityEntry;
import net.minecraft.client.Minecraft;
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

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityWrapper implements IEntityEntry
{
    @Unique
    private final ProblemReporter.Collector problemReporter = new ProblemReporter.ScopedCollector(LOGGER);

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
    public abstract Vec3 position();

    @Shadow
    private int id;

    @Shadow
    public abstract UUID getUUID();

    @Shadow
    public abstract boolean save(ValueOutput output);

    @Shadow
    public abstract RegistryAccess registryAccess();

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    public abstract void saveWithoutId(ValueOutput output);

    @Unique
    private JsonElement cachedName = null;
    @Unique
    private int nameHash = 0;

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
        return position().x;
    }

    @Override
    public double w2k$y()
    {
        return position().y;
    }

    @Override
    public double w2k$z()
    {
        return position().z;
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
        if (Minecraft.getInstance().level == null)
        {
            return null;
        }

        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LOGGER))
        {
            final TagValueOutput lol = TagValueOutput.createWithContext(reporter,
                    registryAccess());

            lol.putString("id", w2k$type());
            saveWithoutId(lol);

            return lol.buildResult().toString();
        }
        catch (Throwable ex)
        {
            W2K.getLogger().error("Failed to save data for entity {}", w2k$uuid().toString(), ex);
            return null;
        }
    }
}
