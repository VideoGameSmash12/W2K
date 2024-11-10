package me.videogamesm12.w2k.toolbox.util;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.EntityEntry;
import net.kyori.adventure.nbt.BinaryTagIO;
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

	public static CompletableFuture<String[][]> performEntityDump(final boolean parallel)
	{
		return CompletableFuture.supplyAsync(() ->
		{
			final List<String> completedEntities = new ArrayList<>();
			final List<String> failedEntities = new ArrayList<>();
			final List<EntityEntry> entry = W2K.getInstance().getDriverManager().getVersionBridge().getNearbyEntities(true);

			final File dumpDir = new File(dumpsFolder, String.valueOf(System.currentTimeMillis()));
			if (!dumpDir.isDirectory())
			{
				dumpDir.mkdirs();
			}

			(parallel ? entry.parallelStream() : entry.stream()).forEach(entity ->
			{
				try (FileOutputStream stream = new FileOutputStream(new File(dumpDir, "entity_" + entity.getId() + "_" + entity.getUuid() + ".nbt")))
				{
					BinaryTagIO.writer().write(TagStringIO.get().asCompound(entity.getNbt()), stream, BinaryTagIO.Compression.GZIP);
					completedEntities.add(entity.getUuid().toString());
				}
				catch (IOException ex)
				{
					failedEntities.add(entity.getUuid().toString());
				}

			});

			return new String[][] {completedEntities.toArray(new String[0]), failedEntities.toArray(new String[0])};
		});
	}
}
