package me.videogamesm12.w2k.drivers.v1_8.mixin.wrapped;

import me.videogamesm12.w2k.kernel.wrapper.WrappedMinecraftClient;
import me.videogamesm12.w2k.kernel.wrapper.entity.player.WrappedPlayerEntity;
import me.videogamesm12.w2k.kernel.wrapper.gui.WrappedScreen;
import me.videogamesm12.w2k.kernel.wrapper.network.WrappedClientPlayNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.FutureTask;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientWrappingMixin implements WrappedMinecraftClient
{
	@Shadow public abstract void setScreen(@Nullable Screen screen);

	@Shadow @Nullable public abstract ClientPlayNetworkHandler getNetworkHandler();

	@Final @Shadow public Profiler profiler;

	@Shadow @Final private Queue<FutureTask<?>> tasks;

	@Shadow public abstract void scheduleStop();

	@Shadow public ClientPlayerEntity player;
	@Unique
	private final Queue<Runnable> w2k$preTickQueue = new ConcurrentLinkedDeque<>();

	@Unique
	private final Queue<Runnable> w2k$postTickQueue = new ConcurrentLinkedDeque<>();

	@Override
	public @Nullable WrappedClientPlayNetworkHandler w2k$getNetworkHandler()
	{
		return (WrappedClientPlayNetworkHandler) getNetworkHandler();
	}

	@Override
	public boolean w2k$isNetworkHandlerPresent()
	{
		return getNetworkHandler() != null;
	}

	@Override
	public @Nullable WrappedPlayerEntity w2k$getPlayer()
	{
		return (WrappedPlayerEntity) player;
	}

	@Override
	public void w2k$setScreen(WrappedScreen screen)
	{
		if (screen != null)
		{
			setScreen((Screen) screen);
		}
		else
		{
			setScreen(null);
		}
	}

	@Override
	public void w2k$queuePreRender(Runnable runnable)
	{
		this.tasks.add(new FutureTask<>(runnable, null));
	}

	@Override
	public void w2k$queuePreTick(Runnable runnable)
	{
		w2k$preTickQueue.add(runnable);
	}

	@Override
	public void w2k$queuePostTick(Runnable runnable)
	{
		w2k$postTickQueue.add(runnable);
	}

	@Override
	public void w2k$scheduleSafeShutdown()
	{
		scheduleStop();
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void injectTickForPreTickProcessing(CallbackInfo ci)
	{
		while (!w2k$preTickQueue.isEmpty())
		{
			w2k$preTickQueue.poll().run();
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V",
			shift = At.Shift.BEFORE))
	public void injectTickForPostTickProcessing(CallbackInfo ci)
	{
		profiler.swap("w2kPostTick");

		while (!w2k$postTickQueue.isEmpty())
		{
			w2k$postTickQueue.poll().run();
		}
	}
}
