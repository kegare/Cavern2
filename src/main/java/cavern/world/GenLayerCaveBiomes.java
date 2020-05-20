package cavern.world;

import java.util.List;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerCaveBiomes extends GenLayer
{
	private final BiomeProvider provider;

	public GenLayerCaveBiomes(BiomeProvider provider, long seed, GenLayer layer)
	{
		this(provider, seed);
		this.parent = layer;
	}

	public GenLayerCaveBiomes(BiomeProvider provider, long seed)
	{
		super(seed);
		this.provider = provider;
	}

	@Override
	public int[] getInts(int x, int z, int width, int depth)
	{
		int dest[] = IntCache.getIntCache(width * depth);

		for (int dz = 0; dz < depth; dz++)
		{
			for (int dx = 0; dx < width; dx++)
			{
				initChunkSeed(dx + x, dz + z);

				dest[dx + dz * width] = Biome.getIdForBiome(getRandomBiome(provider.getBiomesToSpawnIn()));
			}
		}

		return dest;
	}

	private Biome getRandomBiome(List<Biome> biomes)
	{
		return biomes.get(nextInt(biomes.size()));
	}
}