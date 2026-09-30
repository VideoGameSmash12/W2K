package me.videogamesm12.w2k.val.v26_2.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.render.WeatherRenderCheckEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererInjector
{
    @Unique
    private final WeatherRenderCheckEvent weatherRenderCheckEvent = new WeatherRenderCheckEvent();

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void callWeatherRenderCheckEvent(ClientLevel level, float partialTicks, Vec3 cameraPos, WeatherRenderState renderState, CallbackInfo ci)
    {
        W2K.getEventBus().post(weatherRenderCheckEvent.update());

        if (weatherRenderCheckEvent.isCancelled())
        {
            ci.cancel();
        }
    }
}
