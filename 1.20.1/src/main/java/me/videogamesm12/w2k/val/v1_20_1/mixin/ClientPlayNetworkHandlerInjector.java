package me.videogamesm12.w2k.val.v1_20_1.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import me.videogamesm12.w2k.kernel.event.network.packet.*;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerInjector
{
    @Unique
    private final IncomingEntitySpawnPacketEvent entitySpawnPacketEvent = new IncomingEntitySpawnPacketEvent();
    @Unique
    private final IncomingExplosionPacketEvent explosionPacketEvent = new IncomingExplosionPacketEvent();
    @Unique
    private final IncomingLightUpdatePacketEvent lightUpdatePacketEvent = new IncomingLightUpdatePacketEvent();
    @Unique
    private final IncomingParticleSpawnPacketEvent particleSpawnPacketEvent = new IncomingParticleSpawnPacketEvent();
    @Unique
    private final IncomingMapUpdatePacketEvent mapUpdatePacketEvent = new IncomingMapUpdatePacketEvent();
    @Unique
    private final IncomingOpenScreenPacketEvent openScreenPacketEvent = new IncomingOpenScreenPacketEvent();

    @Inject(method = "onEntitySpawn", at = @At("HEAD"), cancellable = true)
    public void callIncomingEntitySpawnPacketEvent(EntitySpawnS2CPacket packet, CallbackInfo ci)
    {
        W2K.getEventBus().post(entitySpawnPacketEvent.update(packet.getId(),
                packet.getUuid(),
                Registries.ENTITY_TYPE.getId(packet.getEntityType()).toString(),
                packet.getX(),
                packet.getY(),
                packet.getZ()));

        if (entitySpawnPacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true)
    public void callIncomingExplosionPacketEvent(ExplosionS2CPacket packet, CallbackInfo ci)
    {
        W2K.getEventBus().post(explosionPacketEvent.update(packet.getRadius(),
                packet.getX(),
                packet.getY(),
                packet.getZ()));

        if (explosionPacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "onLightUpdate", at = @At("HEAD"), cancellable = true)
    public void callIncomingLightUpdatePacketEvent(LightUpdateS2CPacket packet, CallbackInfo ci)
    {
        W2K.getEventBus().post(lightUpdatePacketEvent.update(packet.getChunkX(), packet.getChunkZ()));

        if (lightUpdatePacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
    public void callIncomingParticleSpawnPacketEvent(ParticleS2CPacket packet, CallbackInfo ci)
    {
        W2K.getEventBus().post(particleSpawnPacketEvent.update(packet.getCount(),
                packet.getX(),
                packet.getY(),
                packet.getZ(),
                packet.getSpeed()));

        if (particleSpawnPacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "onMapUpdate", at = @At("HEAD"), cancellable = true)
    public void callIncomingMapUpdatePacketEvent(MapUpdateS2CPacket packet, CallbackInfo ci)
    {
        W2K.getEventBus().post(mapUpdatePacketEvent.update(packet.getId(),
                packet.getScale(),
                packet.isLocked()));

        if (mapUpdatePacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    public void callIncomingOpenScreenPacketEvent(OpenScreenS2CPacket packet, CallbackInfo ci)
    {
        final Identifier id = Registries.SCREEN_HANDLER.getId(packet.getScreenHandlerType());
        W2K.getEventBus().post(openScreenPacketEvent.update(id != null ? id.toString() : "minecraft:unknown",
                Text.Serializer.toJsonTree(packet.getName()),
                packet.getSyncId()));

        if (openScreenPacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }
}
