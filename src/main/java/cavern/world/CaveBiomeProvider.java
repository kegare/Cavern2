package cavern.world;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import cavern.config.manager.CaveBiomeManager;
import cavern.util.CaveUtils;
import cavern.world.gen.GenLayerCaveBiomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

public class CaveBiomeProvider extends BiomeProvider
{
	private final World world;
	private final CaveBiomeCache biomeCache;
	private final CaveBiomeManager biomeManager;

	private Pair<Long, List<Biome>> cachedBiomes;

	public CaveBiomeProvider(World world, CaveBiomeManager biomeManager)
	{
		this.world = world;
		this.biomeCache = new CaveBiomeCache(this, 512, true);
		this.biomeManager = biomeManager;
		this.makeLayers(world.getSeed());
	}

	private void makeLayers(long seed)
	{
		GenLayer layer = new GenLayerCaveBiomes(this, 1L);

		layer = new GenLayerZoom(1000L, layer);
		layer = new GenLayerZoom(1001L, layer);
		layer = new GenLayerZoom(1002L, layer);
		layer = new GenLayerZoom(1003L, layer);
		layer = new GenLayerZoom(1004L, layer);

		GenLayer voronoi = new GenLayerVoronoiZoom(10L, layer);

		layer.initWorldGenSeed(seed);
		voronoi.initWorldGenSeed(seed);

		CaveUtils.setPrivateValue(BiomeProvider.class, this, layer, "genBiomes", "field_76944_d");
		CaveUtils.setPrivateValue(BiomeProvider.class, this, voronoi, "biomeIndexLayer", "field_76945_e");
	}

	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return Lists.newArrayList(biomeManager.getCaveBiomes().keySet());
	}

	public List<Biome> getCachedBiomes()
	{
		long time = world.getWorldInfo().getWorldTotalTime();

		if (cachedBiomes == null || cachedBiomes.getLeft().longValue() + 400L < time)
		{
			cachedBiomes = Pair.of(Long.valueOf(time), getBiomesToSpawnIn());
		}

		return cachedBiomes.getRight();
	}

	@Override
	public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height)
	{
		return getBiomesForGeneration(biomes, x, z, width, height, true);
	}

	public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height, boolean cache)
	{
		if (cache && biomeCache.isGridAligned(x, z, width, height))
		{
			Biome[] cached = biomeCache.getBiomes(x, z);

			return Arrays.copyOf(cached, cached.length);
		}

		return super.getBiomesForGeneration(biomes, x, z, width, height);
	}

	@Override
	public void cleanupCache()
	{
		biomeCache.cleanup();

		super.cleanupCache();
	}
}