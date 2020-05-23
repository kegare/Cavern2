package cavern.world.gen;

import java.util.Random;

import cavern.config.Config;
import cavern.config.manager.CaveVein;
import cavern.world.CaveVeinProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;

public class VeinGenerator
{
	private final CaveVeinProvider provider;

	public VeinGenerator(CaveVeinProvider provider)
	{
		this.provider = provider;
	}

	public void generate(World world, Random rand, Biome[] biomes, ChunkPrimer primer)
	{
		int worldHeight = world.getActualHeight();

		for (CaveVein vein : provider.getVeins())
		{
			if (vein == null || vein.getWeight() <= 0 || vein.getSize() <= 0)
			{
				continue;
			}

			for (int veinCount = 0; veinCount < vein.getWeight(); ++veinCount)
			{
				if (vein.getChance() < 1.0D && rand.nextDouble() < vein.getChance())
				{
					continue;
				}

				int yChance = rand.nextInt(3) + 3;
				int originX = rand.nextInt(16);
				int originY = MathHelper.getInt(rand, vein.getMinHeight(), vein.getMaxHeight());
				int originZ = rand.nextInt(16);
				int x = originX;
				int y = originY;
				int z = originZ;
				Axis prev = null;

				for (int oreCount = 0; oreCount < vein.getSize(); ++oreCount)
				{
					int checkCount = 0;

					while (oreCount > 0)
					{
						Axis next;
						int checkX = x;
						int checkY = y;
						int checkZ = z;

						if (prev == null)
						{
							next = rand.nextInt(yChance) == 0 ? Axis.Y : rand.nextBoolean() ? Axis.X : Axis.Z;
						}
						else switch (prev)
						{
							case X:
								next = rand.nextInt(yChance - 1) == 0 ? Axis.Y : Axis.Z;
								break;
							case Y:
								next = rand.nextBoolean() ? Axis.X : Axis.Z;
								break;
							case Z:
								next = rand.nextInt(yChance - 1) == 0 ? Axis.Y : Axis.X;
								break;
							default:
								next = rand.nextInt(yChance) == 0 ? Axis.Y : rand.nextBoolean() ? Axis.X : Axis.Z;
						}

						switch (next)
						{
							case X:
								if (x <= 0)
								{
									checkX = 1;
								}
								else if (x >= 15)
								{
									checkX = 14;
								}
								else
								{
									checkX = x + (rand.nextBoolean() ? 1 : -1);
								}

								break;
							case Y:
								if (y <= 0)
								{
									checkY = 1;
								}
								else if (y >= worldHeight - 1)
								{
									checkY = worldHeight - 2;
								}
								else
								{
									checkY = y + (rand.nextBoolean() ? 1 : -1);
								}

								break;
							case Z:
								if (z <= 0)
								{
									checkZ = 1;
								}
								else if (z >= 15)
								{
									checkZ = 14;
								}
								else
								{
									checkZ = z + (rand.nextBoolean() ? 1 : -1);
								}

								break;
						}

						IBlockState state = primer.getBlockState(checkX, checkY, checkZ);

						if (state.getBlock() == vein.getTarget().getBlock() && state.getBlock().getMetaFromState(state) == vein.getTarget().getMeta())
						{
							x = checkX;
							y = checkY;
							z = checkZ;

							break;
						}

						if (++checkCount > 10)
						{
							break;
						}
					}

					IBlockState state = primer.getBlockState(x, y, z);

					if (state.getBlock() == vein.getTarget().getBlock() && state.getBlock().getMetaFromState(state) == vein.getTarget().getMeta())
					{
						String[] targetBiomes = vein.getBiomes();

						if (targetBiomes != null && targetBiomes.length > 0)
						{
							Biome biome = biomes[x * 16 + z];

							if (biome == null || Config.containsBiome(targetBiomes, biome))
							{
								continue;
							}
						}

						primer.setBlockState(x, y, z, vein.getBlockMeta().getBlockState());
					}
				}
			}
		}
	}
}