package me.videogamesm12.w2k.integrator.mixins.bleachhack;

import me.videogamesm12.w2k.integrator.Integrator;
import me.videogamesm12.w2k.integrator.core.event.ModuleToggleEvent;
import org.bleachhack.module.Module;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Module.class)
public class BleachHackModuleMixin
{
    @Shadow private boolean enabled;

    @Inject(method = "toggle", at = @At("TAIL"), remap = false)
    public void onToggle(CallbackInfo ci)
    {
        Integrator.getModEventBus("integrator:bleachhack").post(new ModuleToggleEvent<>(this, enabled));
    }
}
