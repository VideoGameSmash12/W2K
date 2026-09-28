package me.videogamesm12.w2k.val.v1_21_11.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.render.WeatherRenderCheckEvent;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererInjector
{
    @Unique
    private final WeatherRenderCheckEvent weatherRenderCheckEvent = new WeatherRenderCheckEvent();

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    public void callWeatherRenderCheckEvent(FrameGraphBuilder frameGraphBuilder, GpuBufferSlice gpuBufferSlice, CallbackInfo ci)
    {
        W2K.getEventBus().post(weatherRenderCheckEvent.update());

        if (weatherRenderCheckEvent.isCancelled())
        {
            ci.cancel();
        }
    }
}
