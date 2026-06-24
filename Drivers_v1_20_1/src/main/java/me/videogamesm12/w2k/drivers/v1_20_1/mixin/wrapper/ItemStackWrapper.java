package me.videogamesm12.w2k.drivers.v1_20_1.mixin.wrapper;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.data.IItemStackEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemStack.class)
public abstract class ItemStackWrapper implements IItemStackEntry
{
    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract int getCount();

    @Shadow
    public abstract int getDamage();

    @Shadow
    private NbtCompound nbt;

    @Shadow
    public abstract NbtCompound writeNbt(NbtCompound par1);

    @Shadow
    public abstract Text getName();

    @Unique
    private int nameHash = 0;
    @Unique
    private JsonElement cachedName = null;
    @Unique
    private String location = null;

    @Override
    public JsonElement w2k$name()
    {
        final Text whatToUse = getName();

        if (cachedName == null || nameHash != whatToUse.hashCode())
        {
            cachedName = Text.Serializer.toJsonTree(whatToUse);
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
        /*if (nbt != null)
        {
            return nbt.toString();
        }

        return null;*/
        return writeNbt(new NbtCompound()).toString();
    }
}
