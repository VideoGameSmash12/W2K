package me.videogamesm12.w2k.drivers.v1_21_4.mixin.wrapping.entity;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.kernel.wrapper.entity.WrappedEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityWrappingMixin implements WrappedEntity
{
	@Shadow public abstract int getId();

	@Shadow public abstract UUID getUuid();

	@Shadow protected abstract @Nullable String getSavedEntityId();

	@Shadow public abstract Text getName();

	@Shadow public abstract DynamicRegistryManager getRegistryManager();

	@Shadow public abstract NbtCompound writeNbt(NbtCompound par1);

	@Shadow public abstract void remove(Entity.RemovalReason par1);

	@Override
	public int w2k$getId()
	{
		return getId();
	}

	@Override
	public UUID w2k$getUuid()
	{
		return getUuid();
	}

	@Override
	public String w2k$getType()
	{
		return getSavedEntityId();
	}

	@Override
	public JsonElement w2k$getName()
	{
		return ComponentUtils.stringToElement(Text.Serialization.toJsonString(getName(), getRegistryManager()));
	}

	@Override
	public String w2k$getNbt()
	{
		return writeNbt(new NbtCompound()).toString();
	}

	@Override
	public void w2k$kill()
	{
		remove(Entity.RemovalReason.KILLED);
	}
}
