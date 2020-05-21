package cavern.config.manager;

import javax.annotation.Nonnull;

import cavern.util.BlockMeta;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;

public class CaveBiome extends WeightedRandom.Item implements Comparable<CaveBiome>
{
	private final Biome biome;

	private BlockMeta terrainBlock;
	private BlockMeta topBlock;

	public CaveBiome(Biome biome, int weight, BlockMeta terrain, BlockMeta top)
	{
		super(weight);
		this.biome = biome;
		this.terrainBlock = terrain;
		this.topBlock = top;
	}

	public CaveBiome(Biome biome, int weight, BlockMeta blockMeta)
	{
		this(biome, weight, blockMeta, blockMeta);
	}

	public CaveBiome(Biome biome, int weight)
	{
		this(biome, weight, null, null);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj == null || !(obj instanceof CaveBiome))
		{
			return false;
		}

		return biome == ((CaveBiome)obj).biome;
	}

	@Override
	public int hashCode()
	{
		return biome.getRegistryName().hashCode();
	}

	@Override
	public int compareTo(CaveBiome o)
	{
		return o == null ? -1 : Integer.compare(Biome.getIdForBiome(biome), Biome.getIdForBiome(o.biome));
	}

	public Biome getBiome()
	{
		return biome;
	}

	public int getWeight()
	{
		return itemWeight;
	}

	public void setWeight(int weight)
	{
		itemWeight = weight;
	}

	@Nonnull
	public BlockMeta getTerrainBlock()
	{
		if (terrainBlock == null)
		{
			setTerrainBlock(new BlockMeta(Blocks.STONE.getDefaultState()));
		}

		return terrainBlock;
	}

	public void setTerrainBlock(BlockMeta terrain)
	{
		terrainBlock = terrain;
	}

	@Nonnull
	public BlockMeta getTopBlock()
	{
		return topBlock == null ? getTerrainBlock() : topBlock;
	}

	public void setTopBlock(BlockMeta top)
	{
		topBlock = top;
	}
}