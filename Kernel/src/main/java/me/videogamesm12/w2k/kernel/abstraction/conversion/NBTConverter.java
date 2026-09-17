package me.videogamesm12.w2k.kernel.abstraction.conversion;

import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.abstraction.BaseVersionAbstractionLayer;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <h1>NBTConverter</h1>
 * <p>An object containing a series of functions which allows you to convert NBT tags between the Adventure NBT and
 *  native Minecraft formats.</p>
 * <p>Instances of this class can be obtained by calling {@link BaseVersionAbstractionLayer#nbt()}.</p>
 * @param <NbtCompound>     {@code NbtCompound} or {@code CompoundTag} (depending on your mappings)
 */
@RequiredArgsConstructor
public class NBTConverter<NbtCompound>
{
    private static final TagStringIO STRING_IO = TagStringIO.get();
    //--
    private final BiFunction<TagStringIO, NbtCompound, CompoundBinaryTag> nativeCompoundToAdventureCompound;
    private final BiFunction<TagStringIO, CompoundBinaryTag, NbtCompound> adventureCompoundToNativeCompound;
    private final Function<NbtCompound, String> nativeToString;

    public CompoundBinaryTag nativeToAdventure(NbtCompound compound)
    {
        return nativeCompoundToAdventureCompound.apply(STRING_IO, compound);
    }

    public NbtCompound adventureToNative(CompoundBinaryTag compound)
    {
        return adventureCompoundToNativeCompound.apply(STRING_IO, compound);
    }

    public String nativeToString(NbtCompound compound)
    {
        return nativeToString.apply(compound);
    }

    public String adventureToString(CompoundBinaryTag compound) throws IOException
    {
        return TagStringIO.get().asString(compound);
    }
}
