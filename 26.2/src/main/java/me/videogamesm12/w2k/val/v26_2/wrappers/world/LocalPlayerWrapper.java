package me.videogamesm12.w2k.val.v26_2.wrappers.world;

import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientPlayerEntityInterface;
import net.kyori.adventure.text.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerWrapper implements ClientPlayerEntityInterface
{
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Override
    public void w2k$displayMessage(Component component)
    {
        minecraft.gui.chatListener().handleSystemMessage((net.minecraft.network.chat.Component) w2k$val().text().adventureToNative(component), false);
    }

    @Override
    public List<ItemStackInterface> w2k$getInventory()
    {
        final Inventory inventory = AbstractClientPlayer.class.cast(this).getInventory();
        final AtomicInteger slot = new AtomicInteger(0);

        return StreamSupport.stream(inventory.spliterator(), false).filter(entry -> {
            slot.getAndIncrement();
            return !entry.isEmpty();
        }).map(entry -> ItemStackInterface.class.cast(entry).w2k$location(String.valueOf(slot))).toList();
    }
}
