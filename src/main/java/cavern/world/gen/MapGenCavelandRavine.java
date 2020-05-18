package cavern.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MapGenCavelandRavine extends MapGenCavernRavine
{
	@Override
	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int x, int z, ChunkPrimer primer)
	{
		if (rand.nextInt(25) == 0)
		{
			int worldHeight = world.getActualHeight();
			double blockX = chunkX * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(worldHeight / 2) + world.provider.getAverageGroundLevel() + 10);
			double blockZ = chunkZ * 16 + rand.nextInt(16);
			float leftRightRadian = rand.nextFloat() * (float)Math.PI * 2.0F;
			float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
			float scale = (rand.nextFloat() * 3.0F + rand.nextFloat()) * 2.0F;

			if (blockY > worldHeight - 40)
			{
				blockY = world.provider.getAverageGroundLevel() + rand.nextInt(10);
			}

			addTunnel(rand.nextLong(), x, z, primer, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 2.0D);
		}
	}

	@Override
	protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop)
	{
		if (y < 7)
		{
			data.setBlockState(x, y, z, BLK_STONE);
		}
		else if (y == 7)
		{
			data.setBlockState(x, y, z, BLK_GRAVEL);
		}
		else if (y < 10)
		{
			Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
			IBlockState state = FLOWING_WATER;

			if (BiomeDictionary.hasType(biome, Type.COLD) && rand.nextInt(3) == 0)
			{
				state = BLK_ICE;
			}

			data.setBlockState(x, y, z, state);
		}
		else
		{
			data.setBlockState(x, y, z, AIR);
		}
	}
}