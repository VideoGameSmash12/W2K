package me.videogamesm12.w2k.toolbox.util;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.*;
import me.videogamesm12.w2k.toolbox.data.DumpResult;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DumpUtil
{
	@Getter
	private static final File dumpsFolder = new File(W2K.getModFolder(), "dumps");

	static
	{
		dumpsFolder.mkdirs();
	}

	public static CompletableFuture<DumpResult> performMapDump(final boolean parallel)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			final List<String> completedMaps = new ArrayList<>();
			final List<String> failedMaps = new ArrayList<>();

			final List<IMapEntry> entries = W2K.getInstance().getDriverManager().getVersionBridge().getMaps();

			final File dumpDir = generateDumpFolder();

			(parallel ? entries.parallelStream() : entries.stream()).forEach(map ->
			{
				String fileName = map.w2k$id();
				try (FileOutputStream stream = new FileOutputStream(new File(dumpDir, fileName + ".dat")))
				{
					BinaryTagIO.writer().write(TagStringIO.get().asCompound(map.w2k$nbt()), stream, BinaryTagIO.Compression.GZIP);
					completedMaps.add(map.w2k$id());
				}
				catch (IOException ex)
				{
					File temp = new File(dumpDir, fileName + ".dat");
					if (temp.exists())
					{
						temp.delete();
					}
					failedMaps.add(map.w2k$id());
				}
			});

			return DumpResult.builder().successful(completedMaps).failed(failedMaps).outputDirectory(dumpDir).build();
		});
	}

	public static CompletableFuture<DumpResult> performEntityDump(final boolean parallel)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			final List<String> completedEntities = new ArrayList<>();
			final List<String> failedEntities = new ArrayList<>();

			final List<IEntityEntry> entry = W2K.getInstance().getDriverManager().getVersionBridge().getEntities();

			final File dumpDir = generateDumpFolder();

			(parallel ? entry.parallelStream() : entry.stream()).forEach(entity ->
			{
				String fileName = "entity_" + entity.w2k$id() + "_" + entity.w2k$uuid();
				try (FileOutputStream stream = new FileOutputStream(new File(dumpDir, fileName + ".nbt")))
				{
					BinaryTagIO.writer().write(TagStringIO.get().asCompound(entity.w2k$data()), stream, BinaryTagIO.Compression.GZIP);
					completedEntities.add(entity.w2k$uuid().toString());
				}
				catch (IOException ex)
				{
					// Fallback to saving files as SNBT
					File temp = new File(dumpDir, fileName +" .nbt");
					if (temp.exists())
					{
						temp.delete();
					}

					try (FileWriter writer = new FileWriter(new File(dumpDir, fileName + ".snbt")))
					{
						writer.write(entity.w2k$data());
					}
					catch (IOException ex2)
					{
						// If both failed, oh well. We tried.
						failedEntities.add(entity.w2k$uuid().toString());
						W2K.getLogger().error("Failed to dump entity ID {}", entity.w2k$id(), ex);
					}
				}

			});

			return DumpResult.builder().successful(completedEntities).failed(failedEntities).outputDirectory(dumpDir).build();
		});
	}

	public static CompletableFuture<DumpResult> performOpenInventoryDump(final boolean parallel)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			final List<String> completedItems = new ArrayList<>();
			final List<String> failedItems = new ArrayList<>();
			final List<String> ignoredItems = new ArrayList<>();

			final List<IItemStackEntry> entry = W2K.getInstance().getDriverManager().getVersionBridge().getOpenInventory();

			final File dumpDir = generateDumpFolder();

			(parallel ? entry.parallelStream() : entry.stream()).forEach(item ->
			{
				if (!item.w2k$isNotEmpty())
				{
					ignoredItems.add(item.w2k$location());
					return;
				}

				String fileName = String.format("item_%s", item.w2k$location());

				try (FileOutputStream stream = new FileOutputStream(new File(dumpDir, fileName + ".nbt")))
				{
					/*final CompoundBinaryTag compound = CompoundBinaryTag.builder()
							.putString("id", item.w2k$type())
							.putInt("Count", item.w2k$count())
							.putInt("Slot", Integer.parseInt(item.w2k$location()))
							.put("tag", item.w2k$data() != null ? TagStringIO.get().asCompound(item.w2k$data())
									: CompoundBinaryTag.empty())
							.build();
					 */
					final CompoundBinaryTag compound = TagStringIO.get().asCompound(item.w2k$data());
					BinaryTagIO.writer().write(compound, stream, BinaryTagIO.Compression.GZIP);
					completedItems.add(item.w2k$location());
				}
				catch (Throwable ex)
				{
					// Fallback to saving files as SNBT
					File temp = new File(dumpDir, fileName +" .nbt");
					if (temp.exists())
					{
						temp.delete();
					}

					try (FileWriter writer = new FileWriter(new File(dumpDir, fileName + ".snbt")))
					{
						writer.write(item.w2k$data());
					}
					catch (IOException ex2)
					{
						// If both failed, oh well. We tried.
						failedItems.add(item.toString());
						W2K.getLogger().error("Failed to dump item in inventory slot {}", item.w2k$location(), ex);

					}
				}

			});

			return DumpResult.builder().successful(completedItems).failed(failedItems).ignored(ignoredItems)
					.outputDirectory(dumpDir).build();
		});
	}

	public static CompletableFuture<DumpResult> performTileEntityDump(final boolean parallel)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			final List<String> completedTiles = new ArrayList<>();
			final List<String> failedTiles = new ArrayList<>();
			final List<String> ignoredTiles = new ArrayList<>();

			final List<IBlockEntityEntry> entry = W2K.getInstance().getDriverManager().getVersionBridge().getBlockEntities();

			final File dumpDir = generateDumpFolder();

			(parallel ? entry.parallelStream() : entry.stream()).forEach(tile ->
			{
				if (tile.w2k$data() == null)
				{
					ignoredTiles.add(tile.toString());
					return;
				}

				String fileName = String.format("tile-entity_%s_%d-%d-%d", tile.w2k$type().replace(":", "-"),
						tile.w2k$x(), tile.w2k$y(), tile.w2k$z());

				try (FileOutputStream stream = new FileOutputStream(new File(dumpDir, fileName + ".nbt")))
				{
					BinaryTagIO.writer().write(TagStringIO.get().asCompound(tile.w2k$data()), stream, BinaryTagIO.Compression.GZIP);
					completedTiles.add(tile.toString());
				}
				catch (IOException ex)
				{
					// Fallback to saving files as SNBT
					File temp = new File(dumpDir, fileName +" .nbt");
					if (temp.exists())
					{
						temp.delete();
					}

					try (FileWriter writer = new FileWriter(new File(dumpDir, fileName + ".snbt")))
					{
						writer.write(tile.w2k$data());
					}
					catch (IOException ex2)
					{
						// If both failed, oh well. We tried.
						failedTiles.add(tile.toString());
						W2K.getLogger().error("Failed to dump tile entity of type {} at {}, {}, {}",
								tile.w2k$type(), tile.w2k$x(), tile.w2k$y(), tile.w2k$z(), ex);
					}
				}

			});

			return DumpResult.builder().successful(completedTiles).failed(failedTiles).ignored(ignoredTiles)
					.outputDirectory(dumpDir).build();
		});
	}

	private static File generateDumpFolder()
	{
		final File dir = new File(dumpsFolder, String.valueOf(System.currentTimeMillis()));
		if (!dir.isDirectory())
		{
			dir.mkdirs();
		}
		return dir;
	}
}
