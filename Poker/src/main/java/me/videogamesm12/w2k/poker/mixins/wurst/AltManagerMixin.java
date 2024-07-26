package me.videogamesm12.w2k.poker.mixins.wurst;

import me.videogamesm12.w2k.poker.Poker;
import me.videogamesm12.w2k.poker.partitions.wurst.event.AltListChangedEvent;
import net.wurstclient.altmanager.AltManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AltManager.class)
public class AltManagerMixin
{
    @Inject(method = "sortAlts", at = @At("TAIL"), remap = false)
    public void injectAltAddedEvent(CallbackInfo ci)
    {
        Poker.getHouse("poker:wurst").post(new AltListChangedEvent());
    }
}
