package cavern.world.gen;

import java.util.function.Supplier;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerCaveBiomes extends GenLayer
{
	private final Supplier<Biome> biome;

	public GenLayerCaveBiomes(Supplier<Biome> biome, long seed, GenLayer layer)
	{
		this(biome, seed);
		this.parent = layer;
	}

	public GenLayerCaveBiomes(Supplier<Biome> biome, long seed)
	{
		super(seed);
		this.biome = biome;
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

				dest[dx + dz * width] = Biome.getIdForBiome(biome.get());
			}
		}

		return dest;
	}
}