package me.videogamesm12.w2k.drivers.v1_21_4.mixin.wrapper;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.data.IEntityEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityWrapper implements IEntityEntry
{
    @Shadow
    public abstract Text getDisplayName();

    @Shadow
    @Nullable
    public abstract Text getCustomName();

    @Shadow
    public abstract Text getName();

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract Vec3d getPos();

    @Shadow
    private int id;

    @Shadow
    public abstract UUID getUuid();

    @Shadow
    public abstract NbtCompound writeNbt(NbtCompound nbt);

    @Shadow
    public abstract String getNameForScoreboard();

    @Unique
    private JsonElement cachedName = null;
    @Unique
    private int nameHash = 0;

    @Override
    public String w2k$internalName()
    {
        return getNameForScoreboard();
    }

    @Override
    public JsonElement w2k$name()
    {
        final Text whatToUse = getDisplayName() != null ?
                getDisplayName() :
                getCustomName() != null ?
                        getCustomName() :
                        getName();

        if (cachedName == null || nameHash != whatToUse.hashCode())
        {
            cachedName = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, whatToUse).result().orElse(null);
            nameHash = whatToUse.hashCode();
        }

        return cachedName;
    }

    @Override
    public String w2k$type()
    {
        return EntityType.getId(getType()).toString();
    }

    @Override
    public double w2k$x()
    {
        return getPos().x;
    }

    @Override
    public double w2k$y()
    {
        return getPos().y;
    }

    @Override
    public double w2k$z()
    {
        return getPos().z;
    }

    @Override
    public int w2k$id()
    {
        return id;
    }

    @Override
    public UUID w2k$uuid()
    {
        return getUuid();
    }

    @Override
    public String w2k$data()
    {
        return writeNbt(new NbtCompound()).toString();
    }
}
