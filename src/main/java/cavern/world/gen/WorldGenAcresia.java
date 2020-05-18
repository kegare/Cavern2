package cavern.world.gen;

import java.util.Random;

import cavern.block.CaveBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenAcresia extends WorldGenerator
{
	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		BlockPos blockpos;

		for (int i = 0; i < 64; ++i)
		{
			blockpos = pos.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

			if (world.isAirBlock(blockpos) && blockpos.getY() < world.getActualHeight() - 1)
			{
				int age;

				if (blockpos.getY() >= world.getActualHeight() / 2)
				{
					age = 3 + rand.nextInt(2);
				}
				else
				{
					age = 2 + rand.nextInt(3);
				}

				IBlockState state = CaveBlocks.ACRESIA.withAge(age);

				if (CaveBlocks.ACRESIA.canBlockStay(world, blockpos, state))
				{
					Material material = world.getBlockState(blockpos.down()).getMaterial();

					if (material == Material.GRASS || material == Material.GROUND)
					{
						world.setBlockState(blockpos, state, 2);
					}
				}
			}
		}

		return true;
	}
}