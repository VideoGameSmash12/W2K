package me.videogamesm12.w2k.val.v26_2.mixin;

import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.network.packet.*;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerInjector
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

    @Inject(method = "handleAddEntity", at = @At("HEAD"), cancellable = true)
    public void callIncomingEntitySpawnPacketEvent(ClientboundAddEntityPacket packet, CallbackInfo ci)
    {
        W2K.getEventBus().post(entitySpawnPacketEvent.update(packet.getId(),
                packet.getUUID(),
                BuiltInRegistries.ENTITY_TYPE.getKey(packet.getType()).toString(),
                packet.getX(),
                packet.getY(),
                packet.getZ()));

        if (entitySpawnPacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "handleExplosion", at = @At("HEAD"), cancellable = true)
    public void callIncomingExplosionPacketEvent(ClientboundExplodePacket packet, CallbackInfo ci)
    {
        W2K.getEventBus().post(explosionPacketEvent.update(packet.radius(),
                packet.center().x,
                packet.center().y,
                packet.center().z));

        if (explosionPacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "handleLightUpdatePacket", at = @At("HEAD"), cancellable = true)
    public void callIncomingLightUpdatePacketEvent(ClientboundLightUpdatePacket packet, CallbackInfo ci)
    {
        W2K.getEventBus().post(lightUpdatePacketEvent.update(packet.getX(), packet.getZ()));

        if (lightUpdatePacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "handleParticleEvent", at = @At("HEAD"), cancellable = true)
    public void callIncomingParticleSpawnPacketEvent(ClientboundLevelParticlesPacket packet, CallbackInfo ci)
    {
        W2K.getEventBus().post(particleSpawnPacketEvent.update(packet.getCount(),
                packet.getX(),
                packet.getY(),
                packet.getZ(),
                packet.getMaxSpeed()));

        if (particleSpawnPacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "handleMapItemData", at = @At("HEAD"), cancellable = true)
    public void callIncomingMapUpdatePacketEvent(ClientboundMapItemDataPacket packet, CallbackInfo ci)
    {
        W2K.getEventBus().post(mapUpdatePacketEvent.update(packet.mapId().id(),
                packet.scale(),
                packet.locked()));

        if (mapUpdatePacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "handleOpenScreen", at = @At("HEAD"), cancellable = true)
    public void callIncomingOpenScreenPacketEvent(ClientboundOpenScreenPacket packet, CallbackInfo ci)
    {
        final Identifier id = BuiltInRegistries.MENU.getKey(packet.getType());
        W2K.getEventBus().post(openScreenPacketEvent.update(id != null ? id.toString() : "minecraft:unknown",
                ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, packet.getTitle()).getOrThrow(),
                packet.getContainerId()));

        if (openScreenPacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "handleContainerContent", at = @At("HEAD"), cancellable = true)
    public void callIncomingInventoryDataPacketEvent(ClientboundContainerSetContentPacket packet, CallbackInfo ci)
    {
        // Also ignore the inventory data tied to the last
        if (openScreenPacketEvent.isCancelled() &&
                openScreenPacketEvent.getSyncId() == packet.containerId())
        {
            ci.cancel();
            return;
        }

        W2K.getEventBus().post(inventoryDataPacketEvent.update(packet.containerId(),
                packet.stateId(),
                (List) packet.items()));
        if (inventoryDataPacketEvent.isCancelled())
        {
            ci.cancel();
        }
    }
}
