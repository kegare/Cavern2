package cavern.world.gen;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBush;

public class WorldGenCaveBush extends WorldGenBush
{
	private final BlockBush block;

	public WorldGenCaveBush(BlockBush block)
	{
		super(block);
		this.block = block;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos position)
	{
		MutableBlockPos pos = new MutableBlockPos();

		for (int y = 10, height = world.getActualHeight() - 10; y < height; ++y)
		{
			pos.setPos(position.getX(), y, position.getZ());
			pos.add(rand.nextInt(8) - rand.nextInt(8), 0, rand.nextInt(8) - rand.nextInt(8));

			if (world.isAirBlock(pos) && block.canBlockStay(world, pos, block.getDefaultState()))
			{
				world.setBlockState(pos, block.getDefaultState(), 2);

				for (int i = 0; i < 8; ++i)
				{
					pos.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));

					if (world.isAirBlock(pos) && block.canBlockStay(world, pos, block.getDefaultState()))
					{
						world.setBlockState(pos, block.getDefaultState(), 2);
					}
				}
			}
		}

		return true;
	}
}