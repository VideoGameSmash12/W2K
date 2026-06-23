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

package me.videogamesm12.w2k.drivers.v26_1.mixin.injector;

import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin
{
    @Inject(method = "handleAddEntity", at = @At("HEAD"), cancellable = true)
    public void onEntitySpawn(ClientboundAddEntityPacket packet, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getNetworkSettings().isIgnoringEntitySpawns())
        {
            ci.cancel();
        }
    }

    @Inject(method = "handleExplosion", at = @At("HEAD"), cancellable = true)
    public void onExplosion(ClientboundExplodePacket packet, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getNetworkSettings().isIgnoringExplosions())
        {
            ci.cancel();
        }
    }

    @Inject(method = "handleLightUpdatePacket", at = @At("HEAD"), cancellable = true)
    public void onLightUpdate(ClientboundLightUpdatePacket packet, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getNetworkSettings().isIgnoringLightUpdates())
        {
            ci.cancel();
        }
    }

    @Inject(method = "handleParticleEvent", at = @At("HEAD"), cancellable = true)
    public void onParticleSpawn(ClientboundLevelParticlesPacket packet, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getNetworkSettings().isIgnoringParticleSpawns())
        {
            ci.cancel();
        }
    }
}