package me.videogamesm12.w2k.toolbox.util;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.EntityEntry;
import me.videogamesm12.w2k.kernel.data.InventoryEntry;
import me.videogamesm12.w2k.kernel.data.TileEntry;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.TagStringIO;

import java.io.*;
import java.util.ArrayList;
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

	public static CompletableFuture<Object[][]> performEntityDump(final boolean parallel)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			final List<String> completedEntities = new ArrayList<>();
			final List<String> failedEntities = new ArrayList<>();
			final List<String> ignoredEntities = new ArrayList<>();
			final List<File> files = new ArrayList<>();

			final List<EntityEntry> entry = W2K.getInstance().getDriverManager().getVersionBridge().getNearbyEntities(true);

			final File dumpDir = new File(dumpsFolder, String.valueOf(System.currentTimeMillis()));
			if (!dumpDir.isDirectory())
			{
				dumpDir.mkdirs();
			}
			files.add(dumpDir);

			(parallel ? entry.parallelStream() : entry.stream()).forEach(entity ->
			{
				String fileName = "entity_" + entity.getId() + "_" + entity.getUuid();
				try (FileOutputStream stream = new FileOutputStream(new File(dumpDir, fileName + ".nbt")))
				{
					BinaryTagIO.writer().write(TagStringIO.get().asCompound(entity.getNbt()), stream, BinaryTagIO.Compression.GZIP);
					completedEntities.add(entity.getUuid().toString());
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
						writer.write(entity.getNbt());
					}
					catch (IOException ex2)
					{
						// If both failed, oh well. We tried.
						failedEntities.add(entity.getUuid().toString());
						W2K.getLogger().error("Failed to dump entity ID {}", entity.getId(), ex);
					}
				}

			});

			return new Object[][] {completedEntities.toArray(new String[0]), failedEntities.toArray(new String[0]), ignoredEntities.toArray(new String[0]), files.toArray(new File[0])};
		});
	}

	public static CompletableFuture<Object[]> performOpenInventoryDump(final boolean parallel)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			final List<String> completedItems = new ArrayList<>();
			final List<String> failedItems = new ArrayList<>();
			final List<String> ignoredItems = new ArrayList<>();

			final List<InventoryEntry> entry = W2K.getInstance().getDriverManager().getVersionBridge().getOpenInventory();

			final File dumpDir = new File(dumpsFolder, String.valueOf(System.currentTimeMillis()));
			if (!dumpDir.isDirectory())
			{
				dumpDir.mkdirs();
			}

			(parallel ? entry.parallelStream() : entry.stream()).forEach(item ->
			{
				if (!item.isNotEmpty())
				{
					ignoredItems.add(item.getLocation());
					return;
				}

				String fileName = String.format("item_%s", item.getLocation());

				try (FileOutputStream stream = new FileOutputStream(new File(dumpDir, fileName + ".nbt")))
				{
					final CompoundBinaryTag compound = CompoundBinaryTag.builder()
							.putString("id", item.getType())
							.putInt("Count", item.getCount())
							.putInt("Slot", Integer.parseInt(item.getLocation()))
							.put("tag", TagStringIO.get().asCompound(item.getData()))
							.build();
					BinaryTagIO.writer().write(compound, stream, BinaryTagIO.Compression.GZIP);
					completedItems.add(item.getLocation());
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
						writer.write(item.getData());
					}
					catch (IOException ex2)
					{
						// If both failed, oh well. We tried.
						failedItems.add(item.toString());
						W2K.getLogger().error("Failed to dump item in inventory slot {}", item.getLocation(), ex);
					}
				}

			});

			// Successful Items, Failed Items, Ignored Items, Directory
			return new Object[] {completedItems.toArray(new String[]{}), failedItems.toArray(new String[]{}),
					ignoredItems.toArray(new String[]{}), dumpDir};
		});
	}

	public static CompletableFuture<Object[]> performTileEntityDump(final boolean parallel)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			final List<String> completedTiles = new ArrayList<>();
			final List<String> failedTiles = new ArrayList<>();
			final List<String> ignoredTiles = new ArrayList<>();

			final List<TileEntry> entry = W2K.getInstance().getDriverManager().getVersionBridge().getNearbyTileEntities();

			final File dumpDir = new File(dumpsFolder, String.valueOf(System.currentTimeMillis()));
			if (!dumpDir.isDirectory())
			{
				dumpDir.mkdirs();
			}

			(parallel ? entry.parallelStream() : entry.stream()).forEach(tile ->
			{
				if (tile.getData() == null)
				{
					ignoredTiles.add(tile.toString());
					return;
				}

				String fileName = String.format("tile-entity_%s_%d-%d-%d", tile.getType().replace(":", "-"),
						tile.getX(), tile.getY(), tile.getZ());

				try (FileOutputStream stream = new FileOutputStream(new File(dumpDir, fileName + ".nbt")))
				{
					BinaryTagIO.writer().write(TagStringIO.get().asCompound(tile.getData()), stream, BinaryTagIO.Compression.GZIP);
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
						writer.write(tile.getData());
					}
					catch (IOException ex2)
					{
						// If both failed, oh well. We tried.
						failedTiles.add(tile.toString());
						W2K.getLogger().error("Failed to dump tile entity of type {} at {}, {}, {}",
								tile.getType(), tile.getX(), tile.getY(), tile.getZ(), ex);
					}
				}

			});

			// Successful Tile Entities, Failed Tile Entities, Ignored Tile Entities, Directory
			return new Object[] {completedTiles.toArray(new String[]{}), failedTiles.toArray(new String[]{}),
					ignoredTiles.toArray(new String[]{}), dumpDir};
		});
	}
}
