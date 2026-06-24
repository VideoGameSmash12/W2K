package me.videogamesm12.w2k.drivers.v26_1.mixin.wrapper;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.data.IItemStackEntry;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public abstract class ItemStackWrapper implements IItemStackEntry
{
    @Shadow
    public abstract Component getDisplayName();

    @Shadow
    public abstract Component getItemName();

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract int count();

    @Shadow
    public abstract int getDamageValue();

    @Shadow
    @Final
    private PatchedDataComponentMap components;

    @Shadow
    public abstract Component getHoverName();

    @Unique
    private int nameHash = 0;
    @Unique
    private JsonElement cachedName = null;
    @Unique
    private String location = null;
    @Unique
    private int componentsHash = 0;
    @Unique
    private CompoundTag cachedData = new CompoundTag();

    @Override
    public JsonElement w2k$name()
    {
        final Component whatToUse = getDisplayName() != null ?
                getDisplayName() :
                getHoverName() != null ?
                        getHoverName() :
                        getItemName();

        if (cachedName == null || nameHash != whatToUse.hashCode())
        {
            cachedName = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, whatToUse).result().orElse(null);
            nameHash = whatToUse.hashCode();
        }

        return cachedName;
    }

    @Override
    public String w2k$type()
    {
        return getItem() != null ? BuiltInRegistries.ITEM.getKey(getItem()).toString() : "minecraft:unknown";
    }

    @Override
    public int w2k$count()
    {
        return count();
    }

    @Override
    public int w2k$damage()
    {
        return getDamageValue();
    }

    @Override
    public String w2k$location()
    {
        return location;
    }

    @Override
    public IItemStackEntry w2k$location(String location)
    {
        this.location = location;
        return this;
    }

    @Override
    public String w2k$data()
    {
        if (components.hashCode() != componentsHash)
        {
            cachedData = (CompoundTag) ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, ItemStack.class.cast(this))
                    .result().orElse(new CompoundTag());
            componentsHash = components.hashCode();
        }

        return cachedData.toString();
    }
}
