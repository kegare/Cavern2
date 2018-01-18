package cavern.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenCavelandCaves extends MapGenCavernCaves
{
	@Override
	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int x, int z, ChunkPrimer primer)
	{
		int worldHeight = world.provider.getActualHeight();
		int heightHalf = worldHeight / 2;
		int chance = rand.nextInt(rand.nextInt(rand.nextInt(30) + 1) + 1);

		for (int i = 0; i < chance; ++i)
		{
			double blockX = chunkX * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(worldHeight - heightHalf) + heightHalf);
			double blockZ = chunkZ * 16 + rand.nextInt(16);
			int count = 1;

			if (rand.nextInt(5) == 0)
			{
				addRoom(rand.nextLong(), x, z, primer, blockX, blockY, blockZ);

				count += rand.nextInt(3);
			}

			for (int j = 0; j < count; ++j)
			{
				float leftRightRadian = rand.nextFloat() * (float)Math.PI ;
				float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float scale = rand.nextFloat() * 8.0F + rand.nextFloat();

				if (rand.nextInt(6) == 0)
				{
					scale *= rand.nextFloat() * rand.nextFloat() * 3.0F + 1.0F;
				}

				addTunnel(rand.nextLong(), x, z, primer, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 1.25D);
			}
		}
	}

	@Override
	protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop, IBlockState state, IBlockState up)
	{
		if (y < 7)
		{
			data.setBlockState(x, y, z, BLK_STONE);
		}
		else if (y < 10)
		{
			data.setBlockState(x, y, z, BLK_DIRT);
		}
		else if (y == 10)
		{
			data.setBlockState(x, y, z, BLK_GRASS);
		}
		else
		{
			data.setBlockState(x, y, z, BLK_AIR);
		}
	}
}