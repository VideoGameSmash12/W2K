package me.videogamesm12.w2k.val.v1_20_1.wrappers.world;

import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientPlayerEntityInterface;
import net.kyori.adventure.text.Component;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityWrapper implements ClientPlayerEntityInterface
{
    @Shadow
    public abstract void sendMessage(Text message);

    @Override
    public void w2k$displayMessage(Component component)
    {
        sendMessage((Text) w2k$val().text().adventureToNative(component));
    }

    @Override
    public List<ItemStackInterface> w2k$getInventory()
    {
        final PlayerInventory inventory = AbstractClientPlayerEntity.class.cast(this).getInventory();
        final List<ItemStackInterface> entries = new ArrayList<>();
        final AtomicInteger slot = new AtomicInteger(0);

        entries.addAll(inventory.main.stream().filter(lol -> {
            slot.getAndIncrement();
            return !lol.isEmpty();
        }).map(entry -> ItemStackInterface.class.cast(entry).w2k$location(String.valueOf(slot))).toList());

        entries.addAll(inventory.armor.stream().filter(lol -> {
            slot.getAndIncrement();
            return !lol.isEmpty();
        }).map(entry -> ItemStackInterface.class.cast(entry).w2k$location(String.valueOf(slot))).toList());

        entries.addAll(inventory.offHand.stream().filter(lol -> {
            slot.getAndIncrement();
            return !lol.isEmpty();
        }).map(entry -> ItemStackInterface.class.cast(entry).w2k$location(String.valueOf(slot))).toList());

        return entries;
    }
}
