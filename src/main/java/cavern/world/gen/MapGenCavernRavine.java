package cavern.world.gen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;

public class MapGenCavernRavine extends MapGenRavine
{
	protected static final IBlockState FLOWING_WATER = Blocks.FLOWING_WATER.getDefaultState();

	protected static final IBlockState BLK_AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState BLK_WATER = Blocks.WATER.getDefaultState();
	protected static final IBlockState BLK_STONE = Blocks.STONE.getDefaultState();
	protected static final IBlockState BLK_GRAVEL = Blocks.GRAVEL.getDefaultState();
	protected static final IBlockState BLK_ICE = Blocks.ICE.getDefaultState();

	private final float[] parabolicField = new float[1024];

	@Override
	protected void addTunnel(long ravineSeed, int chunkX, int chunkZ, ChunkPrimer primer, double blockX, double blockY, double blockZ, float scale, float leftRightRadian, float upDownRadian, int currentY, int targetY, double scaleHeight)
	{
		Random random = new Random(ravineSeed);
		int worldHeight = world.getActualHeight();
		double centerX = chunkX * 16 + 8;
		double centerZ = chunkZ * 16 + 8;
		float leftRightChange = 0.0F;
		float upDownChange = 0.0F;

		if (targetY <= 0)
		{
			int blockRangeY = range * 16 - 16;
			targetY = blockRangeY - random.nextInt(blockRangeY / 4);
		}

		boolean createFinalRoom = false;

		if (currentY == -1)
		{
			currentY = targetY / 2;
			createFinalRoom = true;
		}

		float nextInterHeight = 1.0F;

		for (int y = 0; y < worldHeight; ++y)
		{
			if (y == 0 || random.nextInt(3) == 0)
			{
				nextInterHeight = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
			}

			parabolicField[y] = nextInterHeight * nextInterHeight;
		}

		for (; currentY < targetY; ++currentY)
		{
			double roomWidth = 1.5D + MathHelper.sin(currentY * (float)Math.PI / targetY) * scale * 1.0F;
			double roomHeight = roomWidth * scaleHeight;
			roomWidth *= random.nextFloat() * 0.25D + 0.75D;
			roomHeight *= random.nextFloat() * 0.25D + 0.75D;
			float moveHorizontal = MathHelper.cos(upDownRadian);
			float moveVertical = MathHelper.sin(upDownRadian);
			blockX += MathHelper.cos(leftRightRadian) * moveHorizontal;
			blockY += moveVertical;
			blockZ += MathHelper.sin(leftRightRadian) * moveHorizontal;
			upDownRadian *= 0.7F;
			upDownRadian += upDownChange * 0.05F;
			leftRightRadian += leftRightChange * 0.05F;
			upDownChange *= 0.8F;
			leftRightChange *= 0.5F;
			upDownChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			leftRightChange += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

			if (createFinalRoom || random.nextInt(4) != 0)
			{
				double distanceX = blockX - centerX;
				double distanceZ = blockZ - centerZ;
				double distanceY = targetY - currentY;
				double maxDistance = scale + 2.0F + 16.0F;

				if (distanceX * distanceX + distanceZ * distanceZ - distanceY * distanceY > maxDistance * maxDistance)
				{
					return;
				}

				if (blockX >= centerX - 16.0D - roomWidth * 2.0D && blockZ >= centerZ - 16.0D - roomWidth * 2.0D && blockX <= centerX + 16.0D + roomWidth * 2.0D && blockZ <= centerZ + 16.0D + roomWidth * 2.0D)
				{
					int xLow = Math.max(MathHelper.floor(blockX - roomWidth) - chunkX * 16 - 1, 0);
					int xHigh = Math.min(MathHelper.floor(blockX + roomWidth) - chunkX * 16 + 1, 16);
					int yLow = Math.max(MathHelper.floor(blockY - roomHeight) - 1, 1);
					int yHigh = Math.min(MathHelper.floor(blockY + roomHeight) + 1, worldHeight - 8);
					int zLow = Math.max(MathHelper.floor(blockZ - roomWidth) - chunkZ * 16 - 1, 0);
					int zHigh = Math.min(MathHelper.floor(blockZ + roomWidth) - chunkZ * 16 + 1, 16);

					for (int x = xLow; x < xHigh; ++x)
					{
						double xScale = (chunkX * 16 + x + 0.5D - blockX) / roomWidth;

						for (int z = zLow; z < zHigh; ++z)
						{
							double zScale = (chunkZ * 16 + z + 0.5D - blockZ) / roomWidth;

							if (xScale * xScale + zScale * zScale < 1.0D)
							{
								for (int y = yHigh - 1; y >= yLow; --y)
								{
									double yScale = (y + 0.5D - blockY) / roomHeight;

									if ((xScale * xScale + zScale * zScale) * parabolicField[y] + yScale * yScale / 6.0D < 1.0D)
									{
										digBlock(primer, x, y, z, chunkX, chunkZ, false);
									}
								}
							}
						}
					}

					if (createFinalRoom)
					{
						break;
					}
				}
			}
		}
	}

	@Override
	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int x, int z, ChunkPrimer primer)
	{
		if (rand.nextInt(45) == 0)
		{
			int worldHeight = world.getActualHeight();
			double blockX = chunkX * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(worldHeight / 2) + world.provider.getAverageGroundLevel() + 10);
			double blockZ = chunkZ * 16 + rand.nextInt(16);
			float leftRightRadian = rand.nextFloat() * (float)Math.PI * 2.0F;
			float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
			float scale = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;

			if (blockY > worldHeight - 40)
			{
				blockY = world.provider.getAverageGroundLevel() + rand.nextInt(10);
			}

			addTunnel(rand.nextLong(), x, z, primer, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 3.0D);
		}
	}

	@Override
	protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		if (y - 1 < 10)
		{
			data.setBlockState(x, y, z, FLOWING_LAVA);
		}
		else
		{
			data.setBlockState(x, y, z, AIR);
		}
	}
}