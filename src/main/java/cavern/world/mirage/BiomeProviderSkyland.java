package cavern.world.mirage;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Biomes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerAddIsland;
import net.minecraft.world.gen.layer.GenLayerAddMushroomIsland;
import net.minecraft.world.gen.layer.GenLayerAddSnow;
import net.minecraft.world.gen.layer.GenLayerDeepOcean;
import net.minecraft.world.gen.layer.GenLayerEdge;
import net.minecraft.world.gen.layer.GenLayerFuzzyZoom;
import net.minecraft.world.gen.layer.GenLayerHills;
import net.minecraft.world.gen.layer.GenLayerIsland;
import net.minecraft.world.gen.layer.GenLayerRareBiome;
import net.minecraft.world.gen.layer.GenLayerRemoveTooMuchOcean;
import net.minecraft.world.gen.layer.GenLayerRiver;
import net.minecraft.world.gen.layer.GenLayerRiverInit;
import net.minecraft.world.gen.layer.GenLayerRiverMix;
import net.minecraft.world.gen.layer.GenLayerShore;
import net.minecraft.world.gen.layer.GenLayerSmooth;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraft.world.gen.layer.IntCache;

public class BiomeProviderSkyland extends BiomeProvider
{
	private ChunkGeneratorSettings settings;
	private GenLayer genBiomes;
	private GenLayer biomeIndexLayer;

	private final BiomeCache biomeCache;
	private final List<Biome> biomesToSpawnIn;

	protected BiomeProviderSkyland()
	{
		this.biomeCache = new BiomeCache(this);
		this.biomesToSpawnIn = Lists.newArrayList(allowedBiomes);
	}

	public BiomeProviderSkyland(long seed, WorldType worldType, String options)
	{
		this();

		if (worldType == WorldType.CUSTOMIZED && !options.isEmpty())
		{
			this.settings = ChunkGeneratorSettings.Factory.jsonToFactory(options).build();
		}

		GenLayer[] layers = initializeAllBiomeGenerators(seed, worldType, settings);
		layers = getModdedBiomeGenerators(worldType, seed, layers);
		this.genBiomes = layers[0];
		this.biomeIndexLayer = layers[1];
	}

	public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldType, ChunkGeneratorSettings settings)
	{
		GenLayer layerBase = new GenLayerIsland(1L);
		layerBase = new GenLayerFuzzyZoom(2000L, layerBase);
		GenLayerAddIsland layerIsland = new GenLayerAddIsland(1L, layerBase);
		GenLayerZoom layerZoom = new GenLayerZoom(2001L, layerIsland);
		GenLayerAddIsland layerIsland1 = new GenLayerAddIsland(2L, layerZoom);
		layerIsland1 = new GenLayerAddIsland(50L, layerIsland1);
		layerIsland1 = new GenLayerAddIsland(70L, layerIsland1);
		GenLayerRemoveTooMuchOcean layerOcean = new GenLayerRemoveTooMuchOcean(2L, layerIsland1);
		GenLayerAddSnow layerSnow = new GenLayerAddSnow(2L, layerOcean);
		GenLayerAddIsland layerIsland2 = new GenLayerAddIsland(3L, layerSnow);
		GenLayerEdge layerEdge = new GenLayerEdge(2L, layerIsland2, GenLayerEdge.Mode.COOL_WARM);
		layerEdge = new GenLayerEdge(2L, layerEdge, GenLayerEdge.Mode.HEAT_ICE);
		layerEdge = new GenLayerEdge(3L, layerEdge, GenLayerEdge.Mode.SPECIAL);
		GenLayerZoom layerZoom1 = new GenLayerZoom(2002L, layerEdge);
		layerZoom1 = new GenLayerZoom(2003L, layerZoom1);
		GenLayerAddIsland layerIsland3 = new GenLayerAddIsland(4L, layerZoom1);
		GenLayerAddMushroomIsland layerMushIsland = new GenLayerAddMushroomIsland(5L, layerIsland3);
		GenLayerDeepOcean layerDeepOcean = new GenLayerDeepOcean(4L, layerMushIsland);
		GenLayer layerBase1 = GenLayerZoom.magnify(1000L, layerDeepOcean, 0);
		int biomeSize = 1;
		int riverSize = biomeSize;

		if (settings != null)
		{
			biomeSize = settings.biomeSize;
			riverSize = settings.riverSize;
		}

		if (worldType == WorldType.LARGE_BIOMES)
		{
			biomeSize = 3;
		}

		biomeSize = GenLayer.getModdedBiomeSize(worldType, biomeSize);

		GenLayer layerBase2 = GenLayerZoom.magnify(1000L, layerBase1, 0);
		GenLayerRiverInit layerRiver = new GenLayerRiverInit(100L, layerBase2);
		GenLayer layerBase3 = GenLayerZoom.magnify(1000L, layerRiver, 2);
		GenLayer layerBiomeEdge = worldType.getBiomeLayer(seed, layerBase1, settings);
		GenLayer layerHills = new GenLayerHills(1000L, layerBiomeEdge, layerBase3);
		GenLayer layerBase4 = GenLayerZoom.magnify(1000L, layerRiver, 2);
		layerBase4 = GenLayerZoom.magnify(1000L, layerBase4, riverSize);
		GenLayerRiver layerRiver1 = new GenLayerRiver(1L, layerBase4);
		GenLayerSmooth layerSmooth = new GenLayerSmooth(1000L, layerRiver1);
		layerHills = new GenLayerRareBiome(1001L, layerHills);

		for (int i = 0; i < biomeSize; ++i)
		{
			layerHills = new GenLayerZoom(1000 + i, layerHills);

			if (i == 0)
			{
				layerHills = new GenLayerAddIsland(3L, layerHills);
			}

			if (i == 1 || biomeSize == 1)
			{
				layerHills = new GenLayerShore(1000L, layerHills);
			}
		}

		GenLayerSmooth layerSmooth1 = new GenLayerSmooth(1000L, layerHills);
		GenLayerRiverMix layerRiverMix = new GenLayerRiverMix(100L, layerSmooth1, layerSmooth);
		GenLayer layerMain = new GenLayerVoronoiZoom(10L, layerRiverMix);
		layerRiverMix.initWorldGenSeed(seed);
		layerMain.initWorldGenSeed(seed);

		return new GenLayer[] {layerRiverMix, layerMain, layerRiverMix};
	}

	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return biomesToSpawnIn;
	}

	@Override
	public Biome getBiome(BlockPos pos, Biome biome)
	{
		return biomeCache.getBiome(pos.getX(), pos.getZ(), biome);
	}

	@Override
	public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height)
	{
		IntCache.resetIntCache();

		if (biomes == null || biomes.length < width * height)
		{
			biomes = new Biome[width * height];
		}

		int[] aint = genBiomes.getInts(x, z, width, height);

		try
		{
			for (int i = 0; i < width * height; ++i)
			{
				biomes[i] = Biome.getBiome(aint[i], Biomes.DEFAULT);
			}

			return biomes;
		}
		catch (Throwable e)
		{
			CrashReport report = CrashReport.makeCrashReport(e, "Invalid Biome id");
			CrashReportCategory category = report.makeCategory("RawBiomeBlock");

			category.addCrashSection("biomes[] size", Integer.valueOf(biomes.length));
			category.addCrashSection("x", Integer.valueOf(x));
			category.addCrashSection("z", Integer.valueOf(z));
			category.addCrashSection("w", Integer.valueOf(width));
			category.addCrashSection("h", Integer.valueOf(height));

			throw new ReportedException(report);
		}
	}

	@Override
	public Biome[] getBiomes(@Nullable Biome[] biomes, int x, int z, int width, int length, boolean cache)
	{
		IntCache.resetIntCache();

		if (biomes == null || biomes.length < width * length)
		{
			biomes = new Biome[width * length];
		}

		if (cache && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0)
		{
			Biome[] cachedBiomes = biomeCache.getCachedBiomes(x, z);
			System.arraycopy(cachedBiomes, 0, biomes, 0, width * length);

			return biomes;
		}
		else
		{
			int[] aint = biomeIndexLayer.getInts(x, z, width, length);

			for (int i = 0; i < width * length; ++i)
			{
				biomes[i] = Biome.getBiome(aint[i], Biomes.DEFAULT);
			}

			return biomes;
		}
	}

	@Override
	public boolean areBiomesViable(int x, int z, int radius, List<Biome> allowed)
	{
		IntCache.resetIntCache();

		int i = x - radius >> 2;
		int j = z - radius >> 2;
		int k = x + radius >> 2;
		int l = z + radius >> 2;
		int i1 = k - i + 1;
		int j1 = l - j + 1;
		int[] aint = genBiomes.getInts(i, j, i1, j1);

		try
		{
			for (int k1 = 0; k1 < i1 * j1; ++k1)
			{
				Biome biome = Biome.getBiome(aint[k1]);

				if (!allowed.contains(biome))
				{
					return false;
				}
			}

			return true;
		}
		catch (Throwable throwable)
		{
			CrashReport report = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
			CrashReportCategory category = report.makeCategory("Layer");

			category.addCrashSection("Layer", genBiomes.toString());
			category.addCrashSection("x", Integer.valueOf(x));
			category.addCrashSection("z", Integer.valueOf(z));
			category.addCrashSection("radius", Integer.valueOf(radius));
			category.addCrashSection("allowed", allowed);

			throw new ReportedException(report);
		}
	}

	@Override
	@Nullable
	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random)
	{
		IntCache.resetIntCache();

		int i = x - range >> 2;
		int j = z - range >> 2;
		int k = x + range >> 2;
		int l = z + range >> 2;
		int i1 = k - i + 1;
		int j1 = l - j + 1;
		int[] aint = genBiomes.getInts(i, j, i1, j1);
		BlockPos blockpos = null;
		int k1 = 0;

		for (int l1 = 0; l1 < i1 * j1; ++l1)
		{
			int blockX = i + l1 % i1 << 2;
			int blockZ = j + l1 / i1 << 2;
			Biome biome = Biome.getBiome(aint[l1]);

			if (biomes.contains(biome) && (blockpos == null || random.nextInt(k1 + 1) == 0))
			{
				blockpos = new BlockPos(blockX, 0, blockZ);
				++k1;
			}
		}

		return blockpos;
	}

	@Override
	public void cleanupCache()
	{
		biomeCache.cleanupCache();
	}
}