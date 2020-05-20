package cavern.world;

import java.util.List;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.biome.Biome;

public class CaveBiomeCache
{
	private final Long2ObjectMap<Entry> entryMap = new Long2ObjectOpenHashMap<>();
	private final List<Entry> entries = Lists.newArrayList();
	private final CaveBiomeProvider provider;
	private final int gridSize;
	private final boolean offset;

	private long lastCleanupTime;

	public CaveBiomeCache(CaveBiomeProvider provider, int gridSize, boolean offset)
	{
		this.provider = provider;
		this.gridSize = gridSize;
		this.offset = offset;
	}

	private final class Entry
	{
		final Biome[] biomes = new Biome[gridSize * gridSize];
		final int x, z;
		long lastAccessTime;

		Entry(int x, int z)
		{
			this.x = x;
			this.z = z;

			CaveBiomeCache.this.provider.getBiomesForGeneration(biomes, fromGrid(x), fromGrid(z), gridSize, gridSize, false);
		}
	}

	private Entry getEntry(int x, int z)
	{
		x = toGrid(x);
		z = toGrid(z);

		long key = getKey(x, z);
		Entry entry = this.entryMap.get(key);

		if (entry == null)
		{
			entry = new Entry(x, z);

			entryMap.put(key, entry);
			entries.add(entry);
		}

		entry.lastAccessTime = MinecraftServer.getCurrentTimeMillis();

		return entry;
	}

	public Biome[] getBiomes(int x, int z)
	{
		return getEntry(x, z).biomes;
	}

	public void cleanup()
	{
		long currentTime = MinecraftServer.getCurrentTimeMillis();
		long timeSinceCleanup = currentTime - lastCleanupTime;

		if (timeSinceCleanup > 7500L || timeSinceCleanup < 0L)
		{
			lastCleanupTime = currentTime;

			for (int i = 0; i < entries.size(); ++i)
			{
				Entry entry = entries.get(i);
				long timeSinceAccess = currentTime - entry.lastAccessTime;

				if (timeSinceAccess > 30000L || timeSinceAccess < 0L)
				{
					entries.remove(i--);

					long key = getKey(entry.x, entry.z);

					entryMap.remove(key);
				}
			}
		}
	}

	public boolean isGridAligned(int x, int z, int width, int height)
	{
		return width == gridSize && height == gridSize && gridOffset(x) == 0 && gridOffset(z) == 0;
	}

	private int gridOffset(int n)
	{
		return (n + (offset ? gridSize / 2 : 0)) % gridSize;
	}

	private int toGrid(int n)
	{
		return (n + (offset ? gridSize / 2 : 0)) / gridSize;
	}

	private int fromGrid(int n)
	{
		return n * gridSize - (offset ? gridSize / 2 : 0);
	}

	private long getKey(int x, int z)
	{
		return Integer.toUnsignedLong(x) | Integer.toUnsignedLong(z) << 32;
	}
}