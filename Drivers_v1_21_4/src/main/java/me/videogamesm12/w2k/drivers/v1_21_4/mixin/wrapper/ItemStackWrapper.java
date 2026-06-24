package me.videogamesm12.w2k.drivers.v1_21_4.mixin.wrapper;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.data.IItemStackEntry;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public abstract class ItemStackWrapper implements IItemStackEntry
{
    @Shadow
    public abstract Text getItemName();

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract Text getFormattedName();

    @Shadow
    public abstract int getCount();

    @Shadow
    public abstract int getDamage();

    @Shadow
    @Final
    private MergedComponentMap components;

    @Shadow
    @Nullable
    public abstract Text getCustomName();

    @Unique
    private int nameHash = 0;
    @Unique
    private JsonElement cachedName = null;
    @Unique
    private String location = null;
    @Unique
    private int componentsHash = 0;
    @Unique
    private NbtCompound cachedData = new NbtCompound();

    @Override
    public JsonElement w2k$name()
    {
        final Text whatToUse = getFormattedName() != null ?
                getFormattedName() :
                getCustomName() != null ?
                        getCustomName() :
                        getItemName();

        if (cachedName == null || nameHash != whatToUse.hashCode())
        {
            cachedName = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, whatToUse).result().orElse(null);
            nameHash = whatToUse.hashCode();
        }

        return cachedName;
    }

    @Override
    public String w2k$type()
    {
        return getItem() != null ? Registries.ITEM.getId(getItem()).toString() : "minecraft:unknown";
    }

    @Override
    public int w2k$count()
    {
        return getCount();
    }

    @Override
    public int w2k$damage()
    {
        return getDamage();
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
            cachedData = (NbtCompound) ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, ItemStack.class.cast(this))
                    .result().orElse(new NbtCompound());
            componentsHash = components.hashCode();
        }

        return cachedData.toString();
    }
}
