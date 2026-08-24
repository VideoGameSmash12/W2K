/*
 * Copyright (c) 2023 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.videogamesm12.w2k.drivers.v1_20_1.mixin.injector;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.toolbox.modules.AntiLockup;
import me.videogamesm12.w2k.toolbox.modules.QueryLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
    @Unique
    private long w2k$lastAntiLockupAlert;
    @Unique
    private int w2k$lockupCount;

    @Inject(method = "onEntitySpawn", at = @At("HEAD"), cancellable = true)
    public void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getNetworkSettings().isIgnoringEntitySpawns())
        {
            ci.cancel();
        }
    }

    @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true)
    public void onExplosion(ExplosionS2CPacket packet, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getNetworkSettings().isIgnoringExplosions())
        {
            ci.cancel();
        }
    }

    @Inject(method = "onLightUpdate", at = @At("HEAD"), cancellable = true)
    public void onLightUpdate(LightUpdateS2CPacket packet, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getNetworkSettings().isIgnoringLightUpdates())
        {
            ci.cancel();
        }
    }

    @Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
    public void onParticleSpawn(ParticleS2CPacket packet, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getNetworkSettings().isIgnoringParticleSpawns())
        {
            ci.cancel();
        }
    }

    @Inject(method = "onMapUpdate", at = @At("HEAD"), cancellable = true)
    public void onMapUpdate(MapUpdateS2CPacket packet, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getNetworkSettings().isIgnoringMapUpdates())
        {
            ci.cancel();
        }
    }

    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    public void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci)
    {
        final AntiLockup module = W2K.getInstance().getModuleManager().getModule(AntiLockup.class);
        if (module.isEnabled()
                && packet.getScreenHandlerType() == ScreenHandlerType.GENERIC_9X4
                && packet.getName().contains(Text.literal("Player")))
        {
            ci.cancel();

            if (module.showAlert.get())
            {
                w2k$lockupCount++;

                if ((System.currentTimeMillis() - w2k$lastAntiLockupAlert >= module.alertInterval.get()))
                {
                    final Component message = Component.translatable("w2k.toolbox.module.antilockup.blocked", Component.text(w2k$lockupCount))
                            .color(NamedTextColor.YELLOW);

                    W2K.getInstance().getDriverManager().getVersionBridge().displayMessage(message);

                    w2k$lastAntiLockupAlert = System.currentTimeMillis();
                    w2k$lockupCount = 0;
                }
            }
        }
    }
}