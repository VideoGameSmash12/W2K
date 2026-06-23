package me.videogamesm12.w2k.drivers.v26_1.mixin.wrapper;

import me.videogamesm12.w2k.drivers.v26_1.extra.IDebugScreen;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Mixin(DebugScreenEntryList.class)
public class DebugScreenEntryListWrapper implements IDebugScreen
{
    @Shadow
    @Final
    private Map<Identifier, DebugScreenEntryStatus> allStatuses;

    @Override
    public List<Identifier> w2k$getAllEntries()
    {
        return allStatuses.keySet().stream().toList();
    }
}
