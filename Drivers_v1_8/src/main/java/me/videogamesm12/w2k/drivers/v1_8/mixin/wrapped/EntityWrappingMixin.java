package me.videogamesm12.w2k.drivers.v1_8.mixin.wrapped;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.kernel.wrapper.entity.WrappedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityWrappingMixin implements WrappedEntity
{
	@Shadow public abstract int getEntityId();

	@Shadow public abstract UUID getUuid();

	@Shadow public abstract Text getName();

	@Shadow protected abstract @Nullable String getSavedEntityId();

	@Shadow public abstract void kill();

	@Shadow public abstract boolean saveToNbt(NbtCompound nbt);

	@Override
	public int w2k$getId()
	{
		return getEntityId();
	}

	@Override
	public UUID w2k$getUuid()
	{
		return getUuid();
	}

	@Override
	public JsonElement w2k$getName()
	{
		return ComponentUtils.stringToElement(Text.Serializer.serialize(getName()));
	}

	@Override
	public String w2k$getType()
	{
		return getSavedEntityId();
	}

	@Override
	public String w2k$getNbt()
	{
		NbtCompound compound = new NbtCompound();
		saveToNbt(compound);
		return compound.toString();
	}

	@Override
	public void w2k$kill()
	{
		kill();
	}
}
