package cavern.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenFrostCaves extends MapGenCavernCaves
{
	protected static final IBlockState BLK_ICE = Blocks.ICE.getDefaultState();

	@Override
	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int x, int z, ChunkPrimer primer)
	{
		int chance = rand.nextInt(rand.nextInt(rand.nextInt(15) + 1) + 1);

		if (rand.nextInt(6) != 0)
		{
			chance = 0;
		}

		for (int i = 0; i < chance; ++i)
		{
			double blockX = chunkX * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(120) + 8);
			double blockZ = chunkZ * 16 + rand.nextInt(16);
			int count = 1;

			if (rand.nextInt(4) == 0)
			{
				addRoom(rand.nextLong(), x, z, primer, blockX, blockY, blockZ);

				count += rand.nextInt(4);
			}

			for (int j = 0; j < count; ++j)
			{
				float leftRightRadian = rand.nextFloat() * (float)Math.PI * 2.0F;
				float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float scale = rand.nextFloat() * 2.25F + rand.nextFloat();

				if (rand.nextInt(8) == 0)
				{
					scale *= rand.nextFloat() * rand.nextFloat() * 3.5F + 1.0F;
				}

				addTunnel(rand.nextLong(), x, z, primer, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 1.0D);
			}
		}
	}

	@Override
	protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop, IBlockState state, IBlockState up)
	{
		if (y == 6)
		{
			data.setBlockState(x, y, z, BLK_ICE);
		}
		else if (y < 6)
		{
			data.setBlockState(x, y, z, BLK_WATER);
		}
		else
		{
			data.setBlockState(x, y, z, BLK_AIR);
		}
	}
}