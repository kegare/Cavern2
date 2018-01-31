package cavern.world.gen;

import cavern.config.AquaCavernConfig;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenAquaRavine extends MapGenCavernRavine
{
	@Override
	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int x, int z, ChunkPrimer primer)
	{
		if (rand.nextInt(350) == 0)
		{
			double blockX = chunkX * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(10) + world.provider.getAverageGroundLevel());
			double blockZ = chunkZ * 16 + rand.nextInt(16);

			for (int i = 0; i < 2; ++i)
			{
				float leftRightRadian = rand.nextFloat() * (float)Math.PI * 6.0F;
				float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float scale = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 9.0F;

				addTunnel(rand.nextLong(), x, z, primer, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 20.0D);
			}
		}
	}

	@Override
	protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		int height = world.getActualHeight();
		double depth = AquaCavernConfig.floodDepth;

		if (y < 2 || y > height - 3)
		{
			data.setBlockState(x, y, z, BLK_STONE);
		}
		else if (depth <= 0.0D || depth < 1.0D && y >= MathHelper.floor(height * depth))
		{
			data.setBlockState(x, y, z, BLK_AIR);
		}
		else
		{
			data.setBlockState(x, y, z, BLK_WATER);
		}
	}
}