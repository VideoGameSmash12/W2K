package me.videogamesm12.w2k.integrator.mixins.meteor_client;

import me.videogamesm12.w2k.integrator.Integrator;
import me.videogamesm12.w2k.integrator.core.event.ModuleToggleEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Module.class)
public abstract class MeteorModuleMixin
{
    @Shadow public abstract boolean isActive();

    @Inject(method = "toggle", at = @At("TAIL"), remap = false)
    public void onModuleToggle(CallbackInfo ci)
    {
        Integrator.getModEventBus("integrator:meteor").post(new ModuleToggleEvent<>(this, isActive()));
    }
}
