package me.videogamesm12.w2k.integrator.mixins.replaymod;

import com.replaymod.core.ReplayMod;
import com.replaymod.core.versions.scheduler.SchedulerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ReplayMod.class)
public interface ReplayModAccessor
{
	@Accessor
	public SchedulerImpl getScheduler();
}
