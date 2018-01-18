package cavern.world;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import cavern.config.manager.CaveBiomeManager;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeProvider;

public class BiomeProviderCavern extends BiomeProvider
{
	private final World worldObj;
	private final Random random;
	private final BiomeCache biomeCache;
	private final int biomeSize;
	private final CaveBiomeManager biomeManager;

	public BiomeProviderCavern(World world, int biomeSize, CaveBiomeManager manager)
	{
		this.worldObj = world;
		this.random = new Random(world.getSeed());
		this.biomeCache = new BiomeCache(this);
		this.biomeSize = biomeSize;
		this.biomeManager = manager;
	}

	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return Lists.newArrayList(biomeManager.getCaveBiomes().keySet());
	}

	private Biome getCaveBiomeGenAt(BlockPos pos)
	{
		int chunkX = pos.getX() >> 4;
		int chunkZ = pos.getZ() >> 4;

		if (biomeSize <= 0)
		{
			random.setSeed(ChunkPos.asLong(chunkX, chunkZ) ^ worldObj.getSeed());
		}
		else
		{
			random.setSeed(ChunkPos.asLong((chunkX + 1) / biomeSize, (chunkZ + 1) / biomeSize) ^ worldObj.getSeed());
		}

		return biomeManager.getRandomBiome(random, Biomes.PLAINS);
	}

	@Override
	public Biome getBiome(BlockPos pos)
	{
		return getBiome(pos, Biomes.PLAINS);
	}

	@Override
	public Biome getBiome(BlockPos pos, Biome defaultBiome)
	{
		Biome biome = biomeCache.getBiome(pos.getX(), pos.getZ(), null);

		if (biome == null)
		{
			biome = getCaveBiomeGenAt(pos);
		}

		return biome == null ? defaultBiome : biome;
	}

	@Override
	public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int xSize, int zSize)
	{
		if (biomes == null || biomes.length < xSize * zSize)
		{
			biomes = new Biome[xSize * zSize];
		}

		Arrays.fill(biomes, getCaveBiomeGenAt(new BlockPos(x, 0, z)));

		return biomes;
	}

	@Override
	public Biome[] getBiomes(Biome[] biomes, int x, int z, int xSize, int zSize)
	{
		return getBiomesForGeneration(biomes, x, z, xSize, zSize);
	}

	@Override
	public Biome[] getBiomes(Biome[] biomes, int x, int z, int xSize, int zSize, boolean cache)
	{
		return getBiomesForGeneration(biomes, x, z, xSize, zSize);
	}

	@Override
	public boolean areBiomesViable(int x, int z, int range, List<Biome> list)
	{
		return list.contains(getBiome(new BlockPos(x, 0, z)));
	}

	@Override
	public BlockPos findBiomePosition(int x, int z, int range, List<Biome> list, Random random)
	{
		return new BlockPos(x - range + random.nextInt(range * 2 + 1), 0, z - range + random.nextInt(range * 2 + 1));
	}

	@Override
	public void cleanupCache()
	{
		biomeCache.cleanupCache();
	}
}