package me.videogamesm12.w2k.drivers.v1_8.mixin.injector;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.data.AbstractW2KEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(Entity.class)
public class EntityMixin implements AbstractW2KEntity
{
	@Shadow public double x;

	@Shadow public double y;

	@Shadow public double z;

	@Override
	public JsonElement w2kGetEntityName()
	{
		return null;
	}

	@Override
	public String w2kGetEntityType()
	{
		return "";
	}

	@Override
	public String w2kGetEntityLocation()
	{
		return String.format("%s, %s, %s", x, y, z);
	}

	@Override
	public int w2kGetEntityId()
	{
		return 0;
	}

	@Override
	public UUID w2kGetEntityUuid()
	{
		return null;
	}

	@Override
	public String w2kGetEntityNBT()
	{
		return "";
	}
}
