package me.videogamesm12.w2k.val.v1_21_11.wrappers.world;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
    public abstract Text getDisplayName();

    @Shadow
    @Nullable
    public abstract Text getCustomName();

    @Shadow
    public abstract Text getName();

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract UUID getUuid();

    @Shadow
    public abstract void writeData(WriteView writeView);

    @Shadow
    public abstract DynamicRegistryManager getRegistryManager();

    @Shadow
    public abstract String getNameForScoreboard();

    @Shadow
    public abstract void discard();

    @Shadow
    private Vec3d pos;

    @Shadow
    private int id;

    @Shadow
    private BlockPos blockPos;

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
        return pos.x;
    }

    @Override
    public double w2k$y()
    {
        return pos.y;
    }

    @Override
    public double w2k$z()
    {
        return pos.z;
    }

    @Override
    public BlockPosInterface w2k$blockPos()
    {
        return (BlockPosInterface) blockPos;
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
        try (ErrorReporter.Logging reporter = new ErrorReporter.Logging(LOGGER))
        {
            final NbtWriteView view = NbtWriteView.create(reporter,
                    getRegistryManager());

            view.putString("id", w2k$type());
            writeData(view);

            return view.getNbt().toString();
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
        Objects.requireNonNull(MinecraftClient.getInstance().world).removeEntity(id, Entity.RemovalReason.KILLED);
    }
}
