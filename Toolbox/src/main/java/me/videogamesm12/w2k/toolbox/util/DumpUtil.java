package me.videogamesm12.w2k.toolbox.util;

import com.sun.management.HotSpotDiagnosticMXBean;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.*;
import me.videogamesm12.w2k.toolbox.data.DumpResult;
import net.kyori.adventure.nbt.*;

import javax.management.MBeanServer;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

	public static CompletableFuture<DumpResult> performEntityDump(final Supplier<List<IEntityEntry>> supplier, final boolean parallel)
	{
		final CompletableFuture<DumpResult> future = new CompletableFuture<>();

		CompletableFuture.runAsync(() ->
		{
			final List<String> completedEntities = new ArrayList<>();
			final List<String> failedEntities = new ArrayList<>();
			final File dumpDir = generateDumpFolder();

			final List<IEntityEntry> list = supplier.get();

			(parallel ? list.parallelStream() : list.stream()).forEach(entity ->
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

			future.complete(DumpResult.builder()
					.successful(completedEntities)
					.failed(failedEntities).outputDirectory(dumpDir).build());
		});

		return future;
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

	public static CompletableFuture<File> performStructuredThreadDump()
	{
		final CompletableFuture<File> future = new CompletableFuture<>();

		CompletableFuture.runAsync(() ->
		{
			final File file = new File(getDumpsFolder(), "threads-" + System.currentTimeMillis() + ".dat");
			final CompoundBinaryTag.Builder tag = CompoundBinaryTag.builder();

			W2K.getLogger().error("Starting thread dump");

			for (ThreadInfo thread : ManagementFactory.getThreadMXBean().dumpAllThreads(true, true))
			{
				W2K.getLogger().error("Debug - Dumping thread {}", thread.getThreadName());
				tag.put(thread.getThreadName(), threadToElement(thread));
			}

			try (FileOutputStream stream = new FileOutputStream(file))
			{
				W2K.getLogger().error("Completing thread dump");
				BinaryTagIO.writer().write(tag.build(), stream, BinaryTagIO.Compression.GZIP);
				future.complete(file);
				W2K.getLogger().error("Completed");
			}
			catch (IOException ex)
			{
				future.completeExceptionally(ex);
			}
		});

		return future;
	}

	public static CompletableFuture<File> generateHeapDump(boolean live)
	{
		final CompletableFuture<File> future = new CompletableFuture<>();

		CompletableFuture.runAsync(() ->
		{
			final File file = new File(getDumpsFolder(), "heapdump-" + System.currentTimeMillis() + ".hprof");
			final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			try
			{
				HotSpotDiagnosticMXBean hotspot = ManagementFactory.newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
				hotspot.dumpHeap(file.getAbsolutePath(), live);
				future.complete(file);
			}
			catch (IOException ex)
			{
				future.completeExceptionally(ex);
			}
		});

		return future;
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

	private static CompoundBinaryTag threadToElement(final ThreadInfo thread)
	{
		final CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();
		builder.putLong("id", thread.getThreadId());
		builder.putLong("lockOwnerId", thread.getLockOwnerId());
		builder.putString("state", thread.getThreadState().name());
		builder.putBoolean("suspended", thread.isSuspended());
		builder.putBoolean("native", thread.isInNative());
		builder.put("stacktrace", ListBinaryTag.builder().add(
				Arrays.stream(thread.getStackTrace())
						.map(element -> StringBinaryTag.stringBinaryTag(element.toString()))
						.collect(Collectors.toList())).build());
		builder.putLong("blockedCount", thread.getBlockedCount());
		builder.putLong("blockedTime", thread.getBlockedTime());
		builder.putLong("waitedCount", thread.getWaitedCount());
		builder.putLong("waitedTime", thread.getWaitedTime());
		return builder.build();
	}
}
