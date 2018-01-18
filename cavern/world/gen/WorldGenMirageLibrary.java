package cavern.world.gen;

import java.util.Random;

import cavern.block.CaveBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMirageLibrary extends WorldGenerator
{
	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		while (pos.getY() > 1 && world.isAirBlock(pos))
		{
			pos = pos.down();
		}

		generateAirs(world, rand, pos);
		generateFloor(world, rand, pos);
		generatePillars(world, rand, pos);
		generateBookshelves(world, rand, pos);

		return true;
	}

	protected void generateAirs(World world, Random rand, BlockPos pos)
	{
		BlockPos from = pos.add(3, 0, 2);
		BlockPos to = pos.add(-3, 2, -2);

		BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2));
	}

	protected void generateFloor(World world, Random rand, BlockPos pos)
	{
		BlockPos from = pos.add(4, 0, 3);
		BlockPos to = pos.add(-4, 0, -3);

		BlockPos.getAllInBoxMutable(from, to).forEach(blockPos -> world.setBlockState(blockPos, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2));
	}

	protected void generatePillars(World world, Random rand, BlockPos pos)
	{
		IBlockState state = Blocks.LOG.getDefaultState();

		for (int i = 1; i <= 2; ++i)
		{
			world.setBlockState(pos.add(3, i, 2), state, 2);
			world.setBlockState(pos.add(3, i, -2), state, 2);
			world.setBlockState(pos.add(-3, i, 2), state, 2);
			world.setBlockState(pos.add(-3, i, -2), state, 2);
		}
	}

	protected void generateBookshelves(World world, Random rand, BlockPos pos)
	{
		IBlockState state = CaveBlocks.MIRAGE_BOOKSHELF.getDefaultState();

		for (int i = 1; i <= 2; ++i)
		{
			world.setBlockState(pos.add(3, i, 1), state, 2);
			world.setBlockState(pos.add(3, i, -1), state, 2);
			world.setBlockState(pos.add(-3, i, 1), state, 2);
			world.setBlockState(pos.add(-3, i, -1), state, 2);

			world.setBlockState(pos.add(1, i, 2), state, 2);
			world.setBlockState(pos.add(2, i, 2), state, 2);
			world.setBlockState(pos.add(-1, i, 2), state, 2);
			world.setBlockState(pos.add(-2, i, 2), state, 2);
			world.setBlockState(pos.add(1, i, -2), state, 2);
			world.setBlockState(pos.add(2, i, -2), state, 2);
			world.setBlockState(pos.add(-1, i, -2), state, 2);
			world.setBlockState(pos.add(-2, i, -2), state, 2);
		}
	}
}