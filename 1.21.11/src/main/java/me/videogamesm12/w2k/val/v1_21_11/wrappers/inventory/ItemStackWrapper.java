package me.videogamesm12.w2k.val.v1_21_11.wrappers.inventory;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public abstract class ItemStackWrapper implements ItemStackInterface
{
    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract int getCount();

    @Shadow
    public abstract int getDamage();

    @Shadow
    public abstract Text getName();

    @Shadow
    public abstract Text toHoverableText();

    @Shadow
    public abstract Text getItemName();

    @Shadow
    @Final
    MergedComponentMap components;

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
        final Text whatToUse = toHoverableText() != null ?
                toHoverableText() :
                getName() != null ?
                        getName() :
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
    public ItemStackInterface w2k$location(String location)
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
