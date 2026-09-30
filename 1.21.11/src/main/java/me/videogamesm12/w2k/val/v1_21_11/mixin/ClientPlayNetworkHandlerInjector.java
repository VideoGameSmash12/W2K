package me.videogamesm12.w2k.val.v1_21_11.mixin;

import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.network.packet.*;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.Registries;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

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
    @Unique
    private final IncomingInventoryDataPacketEvent inventoryDataPacketEvent = new IncomingInventoryDataPacketEvent();

    @Inject(method = "onEntitySpawn", at = @At("HEAD"), cancellable = true)
    public void callIncomingEntitySpawnPacketEvent(EntitySpawnS2CPacket packet, CallbackInfo ci)
    {
        W2K.getEventBus().post(entitySpawnPacketEvent.update(packet.getEntityId(),
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
        W2K.getEventBus().post(explosionPacketEvent.update(packet.comp_4594(),
                packet.comp_2883().x,
                packet.comp_2883().y,
                packet.comp_2883().z));

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
        W2K.getEventBus().post(mapUpdatePacketEvent.update(packet.comp_2270().comp_2315(),
                packet.comp_2271(),
                packet.comp_2272()));

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
                TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, packet.getName()).getOrThrow(),
                packet.getSyncId()));

        if (openScreenPacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    public void callIncomingInventoryDataPacketEvent(InventoryS2CPacket packet, CallbackInfo ci)
    {
        // Also ignore the inventory data tied to the last
        if (openScreenPacketEvent.isCancelled() &&
                openScreenPacketEvent.getSyncId() == packet.syncId())
        {
            ci.cancel();
            return;
        }

        W2K.getEventBus().post(inventoryDataPacketEvent.update(packet.syncId(),
                packet.revision(),
                (List) packet.contents()));
        if (inventoryDataPacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }
}
